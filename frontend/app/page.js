'use client';

import { useState, useEffect } from 'react';
import { useAuth } from './hooks/useAuth';
import FlexCard from './components/flex/FlexCard';
import LoadingState from './components/common/LoadingState';
import EmptyState from './components/common/EmptyState';
import { FaDumbbell } from 'react-icons/fa';
import flexService from './services/flexService';

export default function Home() {
  const [flexes, setFlexes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const { getToken } = useAuth();

  useEffect(() => {
    const fetchFlexes = async () => {
      try {
        setIsLoading(true);
        const token = await getToken();
        const posts = await flexService.getPosts(token);
        setFlexes(posts);
      } catch (error) {
        console.error('Error fetching flexes:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchFlexes();
  }, []); // Only run once on mount

  return (
    <div className="max-w-2xl mx-auto">
      {isLoading ? (
        <LoadingState count={2} />
      ) : flexes.length > 0 ? (
        <div className="space-y-6">
          {flexes.map((flex) => (
            <FlexCard key={flex.id} flex={flex} />
          ))}
        </div>
      ) : (
        <EmptyState
          title="No flexes yet"
          message="Be the first to share your achievement!"
          icon={FaDumbbell}
        />
      )}
    </div>
  );
}
