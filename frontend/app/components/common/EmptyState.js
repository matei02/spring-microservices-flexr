import { FaInbox } from 'react-icons/fa';

export default function EmptyState({ 
  title = 'No content found',
  message = 'There\'s nothing to display here yet.',
  icon: Icon = FaInbox
}) {
  return (
    <div className="bg-black rounded-xl border border-zinc-900 p-8 text-center">
      <div className="flex flex-col items-center justify-center gap-4">
        <div className="w-16 h-16 text-zinc-700">
          <Icon className="w-full h-full" />
        </div>
        <div>
          <h3 className="text-lg font-semibold text-white">{title}</h3>
          <p className="text-gray-400 mt-1">{message}</p>
        </div>
      </div>
    </div>
  );
}
