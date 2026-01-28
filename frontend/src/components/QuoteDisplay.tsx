import type { Quote } from '../types.ts';

interface QuoteDisplayProps {
    quote: Quote;
}

// Quote and meta-data of interest will come up when the user loads the page
function QuoteDisplay({ quote }: QuoteDisplayProps) {
    return (
        <div className="quote-display">
            <blockquote>{quote.text}</blockquote>
            <p>-- {quote.author}, {quote.year}</p>
            <p className="book-title">{quote.bookTitle}</p>
            <div>
                <span>↑ {quote.upvotes}</span>
                <span>↓ {quote.downvotes}</span>
            </div>
        </div>
    );
}

export default QuoteDisplay;