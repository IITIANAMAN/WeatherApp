package com.example.weatherapp

import androidx.compose.foundation.gestures.snapping.SnapPosition.Center
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.weatherapp.Api.NetworkResponse
import com.example.weatherapp.Api.WeatherModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource

@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    val weatherData = viewModel.weatherData.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3), // Deep blue
                        Color(0xFF64B5F6), // Light blue
                        Color(0xFFBBDEFB)  // Pale blue
                    )
                )
            )
            .padding(top = 50.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Row with Search and Location
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedVisibility(
                visible = showSearch,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Enter city") },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent, shape = RoundedCornerShape(12.dp)),
                        singleLine = true,
                    )
                    IconButton(
                        onClick = {
                            if (city.isNotBlank()) {
                                viewModel.getDate(city)
                                showSearch = false
                                city = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                }
            }

            if (!showSearch) {
                IconButton(
                    onClick = { showSearch = true },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Open Search",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Add a refresh/location icon button for location fetching (optional)
            IconButton(
                onClick = {
                    // Add your location fetch action here
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Use Current Location",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Default City Buttons with nicer styling
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("New Delhi", "Varanasi", "Jaipur").forEach { cityName ->
                Button(
                    onClick = { viewModel.getDate(cityName) },
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = cityName, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weather result
        when (val result = weatherData.value) {
            is NetworkResponse.Error -> {
                Text(
                    result.message,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            NetworkResponse.Loading -> {
                CircularProgressIndicator(color = Color.White)
            }
            is NetworkResponse.Success<*> -> {
                WeatherDetails(result.data as WeatherModel)
            }
            null -> {}
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location with bigger icon and shadow
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF0D47A1)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${data.location.name}, ${data.location.country}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF0D47A1)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Temperature with shadow effect
            Text(
                text = "${data.current.temp_c}°C",
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1),

            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = data.current.condition.text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Weather Icon with nice size and rounded background
            Image(
                painter = painterResource(id = getWeatherIconResource(data.current.condition.text)),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(140.dp)
                    .background(Color.White.copy(alpha = 0.4f), shape = RoundedCornerShape(80.dp))
                    .padding(8.dp)
            )


            Spacer(modifier = Modifier.height(20.dp))

            // Extra Weather Info Rows with softer background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                WeatherStatRow("Feels Like", "${data.current.feelslike_c}°C", "Humidity", "${data.current.humidity}%")
                WeatherStatRow("Wind", "${data.current.wind_kph} km/h", "UV Index", "${data.current.uv}")
                WeatherStatRow("Visibility", "${data.current.vis_km} km", "Pressure", "${data.current.pressure_mb} mb")
            }
        }
    }

    Text(
        "Last Update: ${data.current.last_updated}",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.7f),
        textAlign = TextAlign.End
    )
}

@Composable
fun WeatherStatRow(label1: String, value1: String, label2: String, value2: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        WeatherInfoItem(label1, value1)
        WeatherInfoItem(label2, value2)
    }
}

@Composable
fun WeatherInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f))
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
fun getWeatherIconResource(condition: String): Int {
    return when (condition.lowercase()) {
        "mist" -> R.drawable.mist
        "rain" -> R.drawable.rain
        "smog" -> R.drawable.smog
        "sunny" -> R.drawable.sunny
        else -> R.drawable.h  // fallback icon
    }
}



//fun WeatherPage(viewModel: WeatherViewModel) {
//    var city by remember { mutableStateOf("") }
//    var showSearch by remember { mutableStateOf(false) }
//    val weatherData = viewModel.weatherData.observeAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFE0F7FA), // Light cyan (top)
//                        Color(0xFFB3E5FC), // Sky blue (middle)
//                        Color(0xFFFFFFFF)  // White (bottom)
//                    )
//                )
//            )
//            .padding(top = 20.dp, start = 8.dp, end = 8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            AnimatedVisibility(
//                visible = showSearch,
//                enter = slideInHorizontally(
//                    initialOffsetX = { fullWidth -> fullWidth },
//                    animationSpec = tween(durationMillis = 300)
//                ),
//                exit = slideOutHorizontally(
//                    targetOffsetX = { fullWidth -> fullWidth },
//                    animationSpec = tween(durationMillis = 300)
//                )
//            ) {
//                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
//                    OutlinedTextField(
//                        value = city,
//                        onValueChange = { city = it },
//                        label = { Text("Enter a city") },
//                        modifier = Modifier.weight(1f)
//                    )
//                    IconButton(
//                        onClick = {
//                            if (city.isNotBlank()) {
//                                viewModel.getDate(city)
//                                showSearch = false // auto-hide
//                                city = ""
//                            }
//                        }
//                    ) {
//                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//                    }
//                }
//            }
//
//            if (!showSearch) {
//                IconButton(
//                    onClick = { showSearch = true },
//                    modifier = Modifier.align(Alignment.CenterVertically)
//                ) {
//                    Icon(imageVector = Icons.Default.Search, contentDescription = "Open Search")
//                }
//            }
//        }
//
//
//        // Default City Buttons
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            listOf("New Delhi", "Varanasi", "Jaipur").forEach { cityName ->
//                Button(onClick = {
//                    viewModel.getDate(cityName)
//                }) {
//                    Text(text = cityName)
//                }
//            }
//        }
//
//        // Weather result
//        when (val result = weatherData.value) {
//            is NetworkResponse.Error -> {
//                Text(result.message)
//            }
//            NetworkResponse.Loading -> {
//                CircularProgressIndicator()
//            }
//            is NetworkResponse.Success<*> -> {
//                WeatherDetails(result.data as WeatherModel)
//            }
//            null -> {}
//        }
//    }
//}
//
//
////@Composable
////fun WeatherDetails(data: WeatherModel) {
////    Column {
////        Row (modifier = Modifier.fillMaxWidth(),
////            horizontalArrangement = Arrangement.Start,
////            verticalAlignment = Alignment.Bottom
////            ){
////            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location",
////                modifier = Modifier.size(35.dp))
////            Text(data.location.name,fontSize = 30.sp,
////                fontWeight = FontWeight.Bold)
////            Spacer(modifier = Modifier.width(8.dp))
////            Text(data.location.country,fontSize = 15.sp, color = Color.Gray)
////
////        }
////        Text(
////            text = "${data.current.temp_c}°C",
////            fontSize = 50.sp,
////            fontWeight = FontWeight.Bold,
////            modifier = Modifier.fillMaxWidth(),
////            textAlign = TextAlign.Center
////        )
////
////        Spacer(modifier = Modifier.height(8.dp))
////
////        AsyncImage(
////            model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
////            contentDescription = "Weather Icon",
////            modifier = Modifier.size(160.dp)
////        )
////        Text(
////            text = data.current.condition.text,
////            fontSize = 20.sp,
////            fontWeight = FontWeight.Bold,
////            modifier = Modifier.fillMaxWidth(),)
////
////
////    }
////}
//@Composable
//fun WeatherDetails(data: WeatherModel) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFe3f2fd))
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Location
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = Icons.Default.LocationOn,
//                    contentDescription = "Location",
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "${data.location.name}, ${data.location.country}",
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 18.sp
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Temperature
//            Text(
//                text = "${data.current.temp_c}°C",
//                fontSize = 48.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//
//            Text(
//                text = data.current.condition.text,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color.DarkGray
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Extra Weather Info
//            Column {
//                WeatherStatRow("Feels Like", "${data.current.feelslike_c}°C", "Humidity", "${data.current.humidity}%")
//                WeatherStatRow("Wind", "${data.current.wind_kph} km/h", "UV Index", "${data.current.uv}")
//                WeatherStatRow("Visibility", "${data.current.vis_km} km", "Pressure", "${data.current.pressure_mb} mb")
//            }
//        }
//    }
//
//    Text("Last Update: ${data.current.last_updated}", modifier = Modifier.fillMaxWidth(),
//        fontSize = 20.sp, color = Color.Gray, textAlign = TextAlign.Right)
//}
//
//@Composable
//fun WeatherStatRow(label1: String, value1: String, label2: String, value2: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        WeatherInfoItem(label1, value1)
//        WeatherInfoItem(label2, value2)
//    }
//}
//
//@Composable
//fun WeatherInfoItem(label: String, value: String) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(text = label, fontSize = 24.sp, color = Color.Gray)
//        Text(text = value, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
//    }
//}
