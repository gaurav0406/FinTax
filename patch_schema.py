import sys

with open("backend/schema.sql", "r") as f:
    content = f.read()

content = content.replace(
    "category VARCHAR(50) NOT NULL CHECK (category IN ('Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy')),",
    "category VARCHAR(50) NOT NULL,"
)

with open("backend/schema.sql", "w") as f:
    f.write(content)
