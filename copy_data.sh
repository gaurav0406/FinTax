#!/bin/bash
while true; do
  if [ -f backend_pipeline/processed_scraped_data.json ]; then
    cp backend_pipeline/processed_scraped_data.json app/src/main/assets/processed_scraped_data.json
    break
  fi
  sleep 2
done
