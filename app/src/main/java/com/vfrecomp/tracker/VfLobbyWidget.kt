package com.vfrecomp.tracker

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class VfLobbyWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("vf_widget_prefs", Context.MODE_PRIVATE)
        val searching = prefs.getInt("searching", 0)
        val waiting = prefs.getInt("waiting", 0)
        val playing = prefs.getInt("playing", 0)
        val krCount = prefs.getInt("kr_count", 0)
        val lastUpdated = prefs.getString("updated_time", "--:--") ?: "--:--"

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .cornerRadius(16.dp)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VF Recomp",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GlanceTheme.colors.onSurface
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = lastUpdated,
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = GlanceTheme.colors.outline
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(label = "Search", count = searching)
                        StatItem(label = "Wait", count = waiting)
                        StatItem(label = "Play", count = playing)
                        StatItem(label = "KR 🇰🇷", count = krCount)
                    }
                }
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun StatItem(label: String, count: Int) {
        Column(
            modifier = GlanceModifier.padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GlanceTheme.colors.primary
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
    }
}

class VfWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = VfLobbyWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdateWorker.startImmediate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork("VfWidgetUpdateWork")
    }
}
