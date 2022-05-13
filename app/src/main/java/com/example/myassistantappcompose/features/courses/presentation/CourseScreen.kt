package com.example.myassistantappcompose.features.courses.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myassistantappcompose.R
import com.example.myassistantappcompose.core.presentation.UiEvent
import com.example.myassistantappcompose.core.presentation.composable.StandardTopBar
import com.example.myassistantappcompose.features.courses.data.CourseEntity
import com.example.myassistantappcompose.features.courses.presentation.components.StandardOutlinedTextField
import com.example.myassistantappcompose.features.destinations.CourseEditScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt

@ExperimentalFoundationApi
@Destination(start = true)
@Composable
fun CourseScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val scaffoldState = rememberScaffoldState()
    val courseState = viewModel.courseState
    val courses by viewModel.courses.collectAsState(emptyList())
    val fabHeight = 72.dp
    val fabHeightPx = with(
        LocalDensity.current
    ) { fabHeight.roundToPx().toFloat() }
    val fabOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = fabOffsetHeightPx.value + delta
                fabOffsetHeightPx.value = newOffset.coerceIn(
                    -fabHeightPx,
                    0f
                )
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect{
            when(it){
                is UiEvent.ShowSnackBar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = it.message,
                        actionLabel = it.actionLabel
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onCourseEvent(CourseEvent.OnUndoDeleteCourse)
                    }
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = { StandardTopBar(title = R.string.courses) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.offset {
                    IntOffset(
                        x = 0,
                        y = -fabOffsetHeightPx.value.roundToInt()
                    )
                },
                onClick = { viewModel.onCourseEvent(CourseEvent.OnShowAddCourseDialog) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_new_course)
                )
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            items(courses) { currentCourse ->
                CourseItem(
                    viewModel = viewModel,
                    currentCourse = currentCourse,
                    navigator = navigator
                )
            }
        }

    }
    if (courseState.showAddCourseDialog) {
        AddCourseDialog(
            viewModel = viewModel,
            title = R.string.fill_out_to_add_course,
            onConfirmedClick = {
                viewModel.onCourseEvent(CourseEvent.OnAddCourseConfirmed)
            }
        )
    }

}


@Composable
fun CourseItem(
    viewModel: CourseViewModel,
    currentCourse: CourseEntity,
    navigator: DestinationsNavigator
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(bottom = 24.dp),
        elevation = 10.dp
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .background(Color(currentCourse.color))
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = currentCourse.courseCode,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row {
                    IconButton(
                        onClick = {navigator.navigate(CourseEditScreenDestination(currentCourse.id))}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit_option),
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { viewModel.onCourseEvent(CourseEvent.OnDeleteCourse(currentCourse))}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete_option),
                            tint = Color.White
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = currentCourse.courseName,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lecturer name: ${currentCourse.courseLecturer}",
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    text = "Credit hours: ${currentCourse.courseHours}",
                    fontSize = 12.sp,
                    color = Color.Black
                )

            }
        }
    }
}

@Composable
fun AddCourseDialog(
    viewModel: CourseViewModel,
    @StringRes title: Int,
    onConfirmedClick: () -> Unit,
) {
    val courseState = viewModel.courseState
    AlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        onDismissRequest = { viewModel.onCourseEvent(CourseEvent.OnDismissAddCourseDialog) },
        text = {
            Column {
                Text(
                    text = stringResource(id = title),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                StandardOutlinedTextField(
                    value = courseState.courseName,
                    label = R.string.course_name,
                    onValueChanged = {
                        viewModel.onCourseEvent(CourseEvent.OnCourseNameChange(it))
                    }
                )
                StandardOutlinedTextField(
                    value = courseState.courseCode,
                    label = R.string.course_code,
                    onValueChanged = {
                        viewModel.onCourseEvent(CourseEvent.OnCourseCodeChange(it))
                    }
                )
                StandardOutlinedTextField(
                    value = courseState.courseHours,
                    label = R.string.credit_hours,
                    onValueChanged = {
                        viewModel.onCourseEvent(CourseEvent.OnCourseHoursChange(it))
                    },
                    keyboardType = KeyboardType.Number
                )
                StandardOutlinedTextField(
                    value = courseState.courseLecturer,
                    label = R.string.lecturer,
                    onValueChanged = {
                        viewModel.onCourseEvent(CourseEvent.OnCourseLecturerChange(it))
                    }
                )
            }
        },
        buttons = {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onConfirmedClick() }
                ) {
                    Text(stringResource(id = R.string.add_label))
                }
                TextButton(
                    onClick = { viewModel.onCourseEvent(CourseEvent.OnDismissAddCourseDialog) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        }
    )
}