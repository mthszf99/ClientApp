import axios from 'axios';

const API_URL = 'http://localhost:8080/produtos';

const produtoService = {
  getAll: async () => {
    try {
      const response = await axios.get(API_URL);
      return response.data;
    } catch (error) {
      console.error('Erro detalhado:', error.response ? error.response.data : error.message);
      throw error; // Repropaga o erro para tratamento no componente
    }
  },

  getById: async (id) => {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  },

  create: async (produto) => {
    const response = await axios.post(API_URL, produto);
    return response.data;
  },

  update: async (id, produto) => {
    const response = await axios.put(`${API_URL}/${id}`, produto);
    return response.data;
  },

  delete: async (id) => {
    await axios.delete(`${API_URL}/${id}`);
  }
};

export default produtoService;