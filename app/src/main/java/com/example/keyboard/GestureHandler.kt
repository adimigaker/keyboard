import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class GestureHandler(context: Context) : View.OnTouchListener {
    private val gestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val diffY = e2.y - e1.y
            if (diffY > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                // Swipe down detected
                onSwipeDown()  
                return true
            }
            return false
        }
    })

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun onSwipeDown() {
        // Handle the swipe down gesture for capitalization
        // Add your code here for capitalization
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100  // Adjust as needed
        private const val SWIPE_VELOCITY_THRESHOLD = 100  // Adjust as needed
    }
}