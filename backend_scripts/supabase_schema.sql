-- Run this SQL in your Supabase SQL Editor to create the financial_news table

CREATE TABLE public.financial_news (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    "summaryWhatHappened" TEXT NOT NULL,
    "summaryWhoImpacted" TEXT NOT NULL,
    "summaryActionableTakeaway" TEXT NOT NULL,
    "summaryText" TEXT NOT NULL,
    category TEXT NOT NULL,
    "financialActionUrl" TEXT,
    "sourceUrl" TEXT NOT NULL,
    "sourceName" TEXT NOT NULL DEFAULT 'Indian Financial Feed',
    "audioUrl" TEXT,
    "imageUrl" TEXT,
    "financialImpactBullets" TEXT,
    "publishedAt" BIGINT,
    "isBookmarked" BOOLEAN DEFAULT FALSE,
    "readCount" INTEGER DEFAULT 1250,
    "shareCount" INTEGER DEFAULT 180
);

-- Note: Depending on your exact insert method, you may need to map Python snake_case or camelCase dict keys to the exact column names. 
-- The Python script sends exactly the camelCase keys because it was built to mirror the Kotlin Entity, so the quotes around column names above are required in PostgreSQL to preserve camelCase.
