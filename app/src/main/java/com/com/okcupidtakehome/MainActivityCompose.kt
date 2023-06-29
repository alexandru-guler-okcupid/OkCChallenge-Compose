package com.com.okcupidtakehome

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.com.okcupidtakehome.theme.BackgroundColor
import com.com.okcupidtakehome.theme.OkCupidTakeHomeTheme
import com.com.okcupidtakehome.theme.Teal700
import com.com.okcupidtakehome.theme.White
import com.com.okcupidtakehome.ui.MainComposeViewModel
import com.com.okcupidtakehome.ui.compose.DragDropViewModel
import com.com.okcupidtakehome.ui.compose.DraggableList
import com.com.okcupidtakehome.ui.compose.MatchScreen
import com.com.okcupidtakehome.ui.compose.ReorderItem
import com.com.okcupidtakehome.ui.compose.SpecialBlendScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivityCompose : AppCompatActivity() {

    private val TAG = "MainActivityCompose"
    private val viewModel: MainComposeViewModel by viewModels()
    private val dragDropViewModel: DragDropViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ogList = ReorderItem.getList()
        setContent {
            OkCupidTakeHomeTheme {
                val state by dragDropViewModel.state.collectAsState()
                Box(modifier = Modifier.fillMaxSize()) {
                    DraggableList(
                        state = state,
                        onMove = dragDropViewModel::onMove,
                        canBeMoved = dragDropViewModel::canBeMoved,
                        onAdd = dragDropViewModel::onAdd,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}

@Composable
private fun OkCupidChallenge(viewModel: MainComposeViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val topBarElevation = 5.dp
        val pages = listOf(
            stringResource(R.string.special_blend),
            stringResource(R.string.match_perc)
        )
        val pagerState = rememberPagerState()
        TopTabBar(
            title = stringResource(R.string.search),
            elevation = topBarElevation,
            pages = pages,
            pagerState = pagerState,
            coroutineScope = rememberCoroutineScope(),
            modifier = Modifier
                .fillMaxWidth()
        )

        HorizontalPager(
            count = pages.size,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
        ) { page ->
            when (page) {
                0 -> SpecialBlendScreen(
                    viewModel = viewModel
                )
                1 -> MatchScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TopTabBar(
    title: String,
    elevation: Dp,
    pages: List<String>,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Surface(elevation = elevation) {
        Column {
            TopAppBar(
                backgroundColor = Teal700,
                modifier = modifier
            ) {
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = title,
                    color = White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TabRow(
                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = Teal700,
                // Override the indicator, using the provided pagerTabIndicatorOffset modifier
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        height = 1.dp,
                        color = White
                    )
                },
            ) {
                // Add tabs for all of our pages
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                color = White,
                                fontSize = 15.sp
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                    )
                }
            }
        }
    }
}
