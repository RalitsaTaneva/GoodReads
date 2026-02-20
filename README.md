# 📚 Goodreads Book Recommender

A Java-based console application designed to provide book insights and personalized recommendations. The project leverages a **Kaggle dataset** containing the 10,000 most recommended books of all time, utilizing basic Machine Learning statistical approaches to analyze book similarities.

## 🚀 Key Features

* **Intelligent Recommendations:** Suggests books similar to a given title based on genre overlap and descriptive content.
* **Complex Search API:** Filter the dataset by author, genres (matching "all" or "any"), and specific keywords within titles and descriptions.
* **Natural Language Processing (NLP):** A custom `Tokenizer` that cleans punctuation, handles multi-white spaces, and filters out a predefined set of 174 English **stop-words** to improve analysis quality.

## 🧠 Similarity Algorithms

The core of the recommender system relies on several mathematical models:

1.  **TF-IDF (Term Frequency-Inverse Document Frequency):** Evaluates the importance of words within book descriptions. It rewards words that are frequent in a specific description but rare across the entire dataset, effectively filtering out generic terms.
    
2.  **Overlap Coefficient:** Used for genre-based similarity. It calculates the intersection of two sets of genres divided by the size of the smaller set.
    
3.  **Composite Similarity:** A hybrid approach that combines multiple calculators (e.g., Genres + Descriptions) using weighted scores to provide a highly accurate final similarity ranking.

---

### How to Run
1. Clone the repository.
2. Ensure `com.opencsv:opencsv:5.9` is added to your project dependencies (via Maven).
3. Use `BookLoader.load()` to initialize the dataset from the provided `goodreads_data.csv`.
