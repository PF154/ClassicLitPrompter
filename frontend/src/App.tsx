import { useState, useEffect } from 'react'
import type { Quote } from './types';
import QuoteDisplay from './components/QuoteDisplay';
import ResponseForm from './components/ResponseForm';
import './App.css'

function App() {
  // Use state so that the page updates when variables change
  const [quote, setQuote] = useState<Quote | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRandomQuote();
  }, []);
  

  // Get a random quote from the backend for the user
  const fetchRandomQuote = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/quotes/random');
      const data = await response.json();
      setQuote(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching quote:', error);
      setLoading(false);
    }
  };

  // Method to be passed to the ResponseForm to define submission behavior
  const handleResponseSubmit = async (text: string) => {
    try {
      if (quote === null) return;
      const response = await fetch(`http://localhost:8080/api/quotes/${quote.id}/responses`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ text: text })
      })

      const data = await response.json();
      console.log('Response submitted:', data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!quote) {
    return <div>No quote available</div>
  }

  // QuoteDisplay will update if a new quote is loaded in
  return (
    <div className="App">
      <h1>Writing Prompt</h1>
      <QuoteDisplay quote={quote} />
      <ResponseForm onSubmit={handleResponseSubmit} />
    </div>
  );
}

export default App;