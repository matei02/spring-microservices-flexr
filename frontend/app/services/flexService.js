import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL
  ? `${process.env.NEXT_PUBLIC_API_URL}/flex`
  : 'http://localhost:8080/flex';

function toBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result.split(',')[1]);
    reader.onerror = error => reject(error);
  });
}

class FlexService {
  // POSTS
  async getPosts(token, page = 0, size = 10) {
    try {
      const response = await axios.get(`${API_URL}/post`, {
        params: { page, size },
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching posts:', error);
      throw error;
    }
  }

  async getPost(id, token) {
    try {
      const response = await axios.get(`${API_URL}/post/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      return response.data;
    } catch (error) {
      console.error('Error fetching post:', error);
      throw error;
    }
  }

  async createPost(postData, token) {
    try {
      let imageBase64 = null;
      if (postData.image) {
        imageBase64 = await toBase64(postData.image);
      }
      const payload = {
        content: postData.content,
        authorId: postData.authorId,
        image: imageBase64 || '',
      };
      const response = await axios.post(`${API_URL}/post`, payload, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error('Error creating post:', error);
      throw error;
    }
  }

  async updatePost(id, postData, token) {
    try {
      let imageBase64 = null;
      if (postData.image) {
        imageBase64 = await toBase64(postData.image);
      }
      const payload = {
        content: postData.content,
        authorId: postData.authorId,
        image: imageBase64 || '',
      };
      const response = await axios.put(`${API_URL}/post/${id}`, payload, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error('Error updating post:', error);
      throw error;
    }
  }

  async deletePost(id, token) {
    try {
      await axios.delete(`${API_URL}/post/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
    } catch (error) {
      console.error('Error deleting post:', error);
      throw error;
    }
  }

  // COMMENTS
  async getComments(token, page = 0, size = 10) {
    try {
      const response = await axios.get(`${API_URL}/comment`, {
        params: { page, size },
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching comments:', error);
      throw error;
    }
  }

  async getComment(id, token) {
    try {
      const response = await axios.get(`${API_URL}/comment/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      return response.data;
    } catch (error) {
      console.error('Error fetching comment:', error);
      throw error;
    }
  }

  async createComment(commentData, token) {
    try {
      const response = await axios.post(`${API_URL}/comment`, commentData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error creating comment:', error);
      throw error;
    }
  }

  async updateComment(id, commentData, token) {
    try {
      const response = await axios.put(`${API_URL}/comment/${id}`, commentData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error updating comment:', error);
      throw error;
    }
  }

  async deleteComment(id, token) {
    try {
      await axios.delete(`${API_URL}/comment/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
    } catch (error) {
      console.error('Error deleting comment:', error);
      throw error;
    }
  }

  // LIKES
  async getLikes(token, page = 0, size = 10) {
    try {
      const response = await axios.get(`${API_URL}/like`, {
        params: { page, size },
        headers: { Authorization: `Bearer ${token}` },
      });
      // Ensure always returns an array
      if (Array.isArray(response.data)) {
        return response.data;
      } else if (response.data && Array.isArray(response.data.content)) {
        return response.data.content;
      } else {
        return [];
      }
    } catch (error) {
      console.error('Error fetching likes:', error);
      throw error;
    }
  }

  async getLike(id, token) {
    try {
      const response = await axios.get(`${API_URL}/like/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      return response.data;
    } catch (error) {
      console.error('Error fetching like:', error);
      throw error;
    }
  }

  async createLike(likeData, token) {
    try {
      const response = await axios.post(`${API_URL}/like`, likeData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error creating like:', error);
      throw error;
    }
  }

  async deleteLike(id, token) {
    try {
      await axios.delete(`${API_URL}/like/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
    } catch (error) {
      console.error('Error deleting like:', error);
      throw error;
    }
  }
}

const flexService = new FlexService();
export default flexService;
