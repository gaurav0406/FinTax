-- Supabase PostgreSQL Table Setup for financial_news

CREATE TABLE IF NOT EXISTS public.financial_news (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    summary JSONB NOT NULL, -- Stores array of 3 bullet points
    summary_text TEXT NOT NULL, -- Plain text <60 words for TTS audio
    category VARCHAR(50) NOT NULL CHECK (category IN ('Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy')),
    financial_action_url TEXT,
    source_url TEXT UNIQUE NOT NULL,
    source_name VARCHAR(100),
    audio_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Index for quick filtering by category and date
CREATE INDEX IF NOT EXISTS idx_financial_news_category ON public.financial_news(category);
CREATE INDEX IF NOT EXISTS idx_financial_news_created_at ON public.financial_news(created_at DESC);

-- Enable Row Level Security (RLS) for public read access
ALTER TABLE public.financial_news ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow public read access" 
ON public.financial_news FOR SELECT 
USING (true);

CREATE POLICY "Allow service role insert and update" 
ON public.financial_news FOR ALL 
USING (true);
