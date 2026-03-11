import { useState, useEffect } from 'react'
import type { Quote, Response } from './types';
import QuoteDisplay from './components/QuoteDisplay';
import ResponseForm from './components/ResponseForm';
import ResponseList from './components/ResponseList';
import './App.css'

function App() {
  // Use state so that the page updates when variables change
  const [quote, setQuote] = useState<Quote | null>(null);
  const [loading, setLoading] = useState(true);
  const [hasSubmitted, setHasSubmitted] = useState(false);
  const [responses, setResponses] = useState<Response[]>([]);

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


  const fetchResponses = async (quoteId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/quotes/${quoteId}/responses`);
      const data = await response.json();
      setResponses(data);
    }
    catch (error)
    {
      console.error('Error fetching responses:', error);
    }
  }

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

      console.log('Submit response status:', response.status);
      console.log('Submit response ok:', response.ok);

      if (response.ok) {
        setHasSubmitted(true);
        await fetchResponses(quote.id);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleVote = async (responseId: number, voteType: 'UP' | 'DOWN') => {
    try {
      if (responseId == null || quote === null) return;

      const response = await fetch(`http://localhost:8080/api/responses/${responseId}/vote`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ voteType: voteType })
      });

      if (response.ok) {
        // Refetch responses to get updated vote counts
        await fetchResponses(quote.id);
      }
    } catch (error) {
      console.error('Error in casting vote:', error);
    }
  }

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
      {!hasSubmitted ? (
        <ResponseForm onSubmit={handleResponseSubmit} />
      ) : (
        <ResponseList responses={responses} onVote={handleVote} />
      )}
    </div>
  );
}

export default App;