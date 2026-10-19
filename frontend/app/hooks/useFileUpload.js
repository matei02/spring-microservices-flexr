import { useState, useCallback } from 'react';

export const useFileUpload = (acceptedTypes = ['image/*', 'video/*']) => {
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [error, setError] = useState(null);

  const handleFileSelect = useCallback((selectedFile) => {
    if (!selectedFile) {
      setError('No file selected');
      return;
    }

    // Check file type
    const fileType = selectedFile.type;
    const isAccepted = acceptedTypes.some(type => {
      const [category, extension] = type.split('/');
      return fileType.startsWith(category) && (extension === '*' || fileType.endsWith(extension));
    });

    if (!isAccepted) {
      setError(`File type not accepted. Please upload: ${acceptedTypes.join(', ')}`);
      return;
    }

    // Check file size (max 10MB)
    if (selectedFile.size > 10 * 1024 * 1024) {
      setError('File size too large. Maximum size is 10MB');
      return;
    }

    // Create preview
    const objectUrl = URL.createObjectURL(selectedFile);
    setPreview(objectUrl);
    setFile(selectedFile);
    setError(null);

    return () => URL.revokeObjectURL(objectUrl);
  }, [acceptedTypes]);

  const clearFile = useCallback(() => {
    if (preview) {
      URL.revokeObjectURL(preview);
    }
    setFile(null);
    setPreview(null);
    setError(null);
  }, [preview]);

  return {
    file,
    preview,
    error,
    handleFileSelect,
    clearFile,
  };
};
