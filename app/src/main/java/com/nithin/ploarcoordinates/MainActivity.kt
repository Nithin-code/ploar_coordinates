package com.nithin.ploarcoordinates

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nithin.ploarcoordinates.ui.theme.PloarCoordinatesTheme
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PloarCoordinatesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(paddingValues = innerPadding)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    paddingValues: PaddingValues
){

    var points by remember {
        mutableIntStateOf(0)
    }

    var isTimerRunning by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = "Points: $points",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = {
                isTimerRunning = !isTimerRunning
                points = 0
            }) {
                Text(
                    text = if (isTimerRunning) "Reset" else "Start"
                )
            }
            CountDownTimer(
                isTimerRunning = isTimerRunning
            ){
                isTimerRunning = false
            }

        }

        BallClicker(
            enabled = isTimerRunning
        ){
            points++
        }

    }
}

@Composable
fun CountDownTimer(
    time : Int = 30000,
    isTimerRunning : Boolean = false,
    onTimerEnd : () -> Unit = {}
) {
    var currentTime by remember {
        mutableIntStateOf(time)
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if(!isTimerRunning){
            currentTime = time
            return@LaunchedEffect
        }
        if (currentTime>0){
            delay(1000L)
            currentTime -= 1000
        }else{
            onTimerEnd()
        }
    }

    Text(
        text = (currentTime/1000).toString(),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

}




@Composable
fun BallClicker(
    radius : Float = 100f,
    enabled : Boolean = false,
    ballColor : Color = Color.Red,
    onBallClick : () -> Unit = {}
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ){
        var ballPosition by remember {
            mutableStateOf(randomOffset.invoke(
                radius,
                constraints.maxWidth,
                constraints.maxHeight
            ))
        }

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures {
                    val distance = sqrt(
                        (it.x - ballPosition.x).pow(2) +
                                (it.y - ballPosition.y).pow(2)
                    )
                    if (distance <= radius) {
                        ballPosition = randomOffset.invoke(
                            radius,
                            constraints.maxWidth,
                            constraints.maxHeight
                        )
                        onBallClick()
                    }

                }
            }
        ) {
            drawCircle(
                color = ballColor,
                radius = radius,
                center = ballPosition
            )
        }
    }


}

val randomOffset : (radius : Float , width : Int, height: Int) -> Offset = { _radius , _width , _height ->
    Offset(
        x = Random.nextInt(
            _radius.roundToInt(),
            (_width - _radius).roundToInt()
        ).toFloat(),
        y = Random.nextInt(
            _radius.roundToInt(),
            (_height - _radius).roundToInt()
        ).toFloat(),
    )
}