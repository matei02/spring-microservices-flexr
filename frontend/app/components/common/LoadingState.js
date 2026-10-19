import PostCardSkeleton from '../flex/PostCardSkeleton';

export default function LoadingState({ count = 3 }) {
  return (
    <div className="space-y-6">
      {Array.from({ length: count }).map((_, index) => (
        <PostCardSkeleton key={index} />
      ))}
    </div>
  );
}
