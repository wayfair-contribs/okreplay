package okreplay.sample

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.base.Joiner
import com.google.common.collect.FluentIterable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
  private var textMessage: TextView? = null
  private val itemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_activity -> return@OnNavigationItemSelectedListener goToActivity()
      R.id.navigation_repositories -> return@OnNavigationItemSelectedListener goToRepositories()
      R.id.navigation_organizations -> return@OnNavigationItemSelectedListener goToOrganizations()
    }
    false
  }

  private fun goToOrganizations(): Boolean {
    textMessage!!.setText(R.string.title_organizations)
    return true
  }

  private fun goToRepositories(): Boolean {
    textMessage!!.setText(R.string.title_repositories)
    val application = application as SampleApplication
    application.graph.service.repo(USERNAME, REPO)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          textMessage!!.text =
              "${getString(R.string.title_repositories)}: \n${it.body()?.let { it1 -> it1.name() + ": " + it1.description() }}"
        }, {
          Log.e(TAG, "Request failed: ${it.message}", it)
        })
    return true
  }

  private fun reposToString(repositories: List<Repository>): String {
    return Joiner.on("\n\n").join(FluentIterable.from(repositories)
        .transform { r -> r!!.name() + ": " + r.description() }
        .toList())
  }

  private fun goToActivity(): Boolean {
    textMessage!!.setText(R.string.title_activity)
    return true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    textMessage = findViewById(R.id.message)
    val navigation = findViewById<BottomNavigationView>(R.id.navigation)
    navigation.setOnNavigationItemSelectedListener(itemSelectedListener)
  }

  companion object {
    private const val TAG = "MainActivity"
    private const val USERNAME = "felipecsl"
    private const val REPO = "6502Android"
  }
}
