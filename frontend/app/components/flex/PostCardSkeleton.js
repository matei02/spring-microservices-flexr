export default function PostCardSkeleton() {
  return (
    <div className="bg-black rounded-xl border border-zinc-900 mb-6 overflow-hidden animate-pulse">
      {/* Header skeleton */}
      <div className="flex items-center p-4">
        <div className="w-10 h-10 bg-zinc-900 rounded-full"></div>
        <div className="ml-3">
          <div className="h-4 bg-zinc-900 rounded w-24"></div>
          <div className="h-3 bg-zinc-900 rounded w-16 mt-2"></div>
        </div>
      </div>

      {/* Image skeleton */}
      <div className="aspect-square bg-zinc-900"></div>

      {/* Actions skeleton */}
      <div className="p-4">
        <div className="flex space-x-4 mb-4">
          <div className="w-8 h-8 bg-zinc-900 rounded"></div>
          <div className="w-8 h-8 bg-zinc-900 rounded"></div>
          <div className="w-8 h-8 bg-zinc-900 rounded"></div>
        </div>

        {/* Content skeleton */}
        <div className="space-y-2">
          <div className="h-4 bg-zinc-900 rounded w-3/4"></div>
          <div className="h-4 bg-zinc-900 rounded w-1/2"></div>
        </div>
      </div>
    </div>
  );
}
