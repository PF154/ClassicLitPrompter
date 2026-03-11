import type { Response } from "../types";

interface ResponseListProps {
    responses: Response[];
    onVote: (responseId: number, voteType: 'UP' | 'DOWN') => void;
}

function ResponseList({ responses, onVote }: ResponseListProps) {
    const handleVote = (responseId: number, voteType: 'UP' | 'DOWN') => {
        onVote(responseId, voteType);
    };

    return (
        <div>
            {responses.length > 0 ? (
                responses.map(response => (
                    <div key={response.id}>
                        {response.isEdited ? (
                            <p>Edited</p>
                        ) : (<></>)}
                        <p>{response.text}</p>
                        <p>Votes: {response.upvotes - response.downvotes}</p>
                        <p>Created on: {response.createdAt}</p>
                        <button name="Upvote" onClick={() => handleVote(response.id, 'UP')}>^</button>
                        <button name="Downvote" onClick={() => handleVote(response.id, 'DOWN')}>v</button>
                    </div>
                ))    
            ) : (
                <p>No responses yet</p>
            )}
        </div>
    )

}

export default ResponseList;