package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PythonPipelineTab(
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    var isSimulatingPipeline by remember { mutableStateOf(false) }
    val simulationLogs = remember { mutableStateListOf<String>() }

    val pythonCodeSnippet = """
# financial_news_scraper.py
import feedparser, requests, json, os, time, hashlib
from bs4 import BeautifulSoup
import google.generativeai as genai
from gtts import gTTS
from supabase import create_client

# Indian Financial Feeds
FEEDS = [
    {"name": "Economic Times Wealth", "url": "https://economictimes.indiatimes.com/wealth/rssfeeds/1254212.cms"},
    {"name": "LiveMint Money", "url": "https://www.livemint.com/rss/money"},
    {"name": "Moneycontrol PF", "url": "https://www.moneycontrol.com/rss/personalfinance.xml"},
    {"name": "RBI Press Releases", "url": "https://rbi.org.in/rssfeed.xml"}
]

SYSTEM_PROMPT = ""\"
Output strictly JSON:
{
  "title": "Max 10 words headline",
  "summary": ["Point 1: What happened", "Point 2: Who impacted", "Point 3: Actionable Takeaway"],
  "category": "One of ['Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy']",
  "financial_action_url": "Optional link",
  "source_url": "Original link"
}
Constraints: Total summary <60 words across 3 points.
""\"

def run_pipeline():
    supabase = create_client(os.getenv("SUPABASE_URL"), os.getenv("SUPABASE_KEY"))
    model = genai.GenerativeModel("gemini-2.5-flash")
    
    for feed in FEEDS:
        parsed = feedparser.parse(feed['url'])
        for item in parsed.entries[:3]:
            # Clean HTML & Extract Body
            soup = BeautifulSoup(item.description, 'html.parser')
            text = soup.get_text()
            
            # Gemini NLP Structured Output
            res = model.generate_content(
                f"Title: {item.title}\nContent: {text}",
                generation_config={"response_mime_type": "application/json"}
            )
            data = json.loads(res.text)
            
            # gTTS Speech Audio Generation (<60 words)
            tts = gTTS(text=" ".join(data['summary']), lang='en', tld='co.in')
            audio_file = f"audio/news_{hashlib.md5(item.link.encode()).hexdigest()[:10]}.mp3"
            tts.save(audio_file)
            
            # Save to Supabase table
            supabase.table("financial_news").upsert(data, on_conflict="source_url").execute()
            time.sleep(1.5) # Rate limiting delay
"""

    val sqlSchemaSnippet = """
-- Supabase PostgreSQL Table Setup
CREATE TABLE IF NOT EXISTS public.financial_news (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    summary JSONB NOT NULL,
    summary_text TEXT NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy')),
    financial_action_url TEXT,
    source_url TEXT UNIQUE NOT NULL,
    source_name VARCHAR(100),
    audio_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Indexing for fast queries
CREATE INDEX idx_financial_news_category ON public.financial_news(category);
"""

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("python_pipeline_tab")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MinimalPurpleLightContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = MinimalPurpleDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Production Python Backend Pipeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurpleDark
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Built with feedparser, BeautifulSoup4, Gemini 2.5 Flash, gTTS audio synthesis, and Supabase PostgreSQL.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sub-tabs
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.Transparent,
            contentColor = MinimalPurplePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = MinimalPurplePrimary
                )
            }
        ) {
            Tab(selected = selectedSubTab == 0, onClick = { selectedSubTab = 0 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Python Script", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
            Tab(selected = selectedSubTab == 1, onClick = { selectedSubTab = 1 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Supabase Schema", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSubTab == 0) {
            Button(
                onClick = {
                    simulationLogs.clear()
                    isSimulatingPipeline = true
                    simulationLogs.add("🚀 Starting Python Pipeline Execution...")
                    simulationLogs.add("📡 Scraping Economic Times Wealth RSS Feed...")
                    simulationLogs.add("🤖 Invoking Gemini 2.5 Flash with System Prompt (JSON Schema)...")
                    simulationLogs.add("✅ Title: 'ITR Filing Deadline & New Section 87A Rebate Rules'")
                    simulationLogs.add("🔊 Generating gTTS audio (en-IN) -> audio/news_a3f89d.mp3")
                    simulationLogs.add("💾 Upserting into Supabase PostgreSQL table 'financial_news'...")
                    simulationLogs.add("⏱️ Rate limit pause (1.5s delay)...")
                    simulationLogs.add("🎉 Pipeline finished successfully!")
                    isSimulatingPipeline = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("run_python_simulation_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary),
                shape = RoundedCornerShape(50)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Run Test Pipeline Simulation", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }

            if (simulationLogs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "TERMINAL LOG OUTPUT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MinimalPurpleLightContainer)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        simulationLogs.forEach { log ->
                            Text(
                                text = log,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFF00FF66)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Code Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181A1F))
            ) {
                Text(
                    text = pythonCodeSnippet.trim(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFD4D4D4),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181A1F))
            ) {
                Text(
                    text = sqlSchemaSnippet.trim(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF80CBC4),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
