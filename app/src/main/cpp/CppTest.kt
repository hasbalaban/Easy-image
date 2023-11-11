import com.example.easy_image.view.MainActivity
import kotlin.system.measureTimeMillis

// test cpp and kotlin
fun yNativeFunction(a: Int, b: Int): Int {
    for (i in 0..100000) {
        if (i == 999) {
            return i
        }
    }
    return 0
}
private fun testcpp(){
    //myNativeFunction(1,2)
    val time = measureTimeMillis {
        for (i in 0..1){
            //myNativeFunction(1,1)
        }
    }
    println(time)
}