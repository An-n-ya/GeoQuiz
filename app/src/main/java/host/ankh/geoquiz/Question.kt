package host.ankh.geoquiz

import androidx.annotation.StringRes

data class Question(@StringRes val textResId: Int, var answer: Boolean) {


}