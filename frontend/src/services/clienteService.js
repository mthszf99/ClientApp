import axios from 'axios';

const API_URL = 'http://localhost:8080/clientes';

const clienteService = {
  getAll: async () => {
    try {
      const response = await axios.get(API_URL);
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar clientes:', error);
      throw error;
    }
  },

  getById: async (id) => {
    try {
      const response = await axios.get(`${API_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Erro ao buscar cliente com ID ${id}:`, error);
      throw error;
    }
  },

  create: async (cliente) => {
    try {
      const response = await axios.post(API_URL, cliente);
      return response.data;
    } catch (error) {
      console.error('Erro ao criar cliente:', error);
      throw error;
    }
  },

  update: async (id, cliente) => {
    try {
      const response = await axios.put(`${API_URL}/${id}`, cliente);
      return response.data;
    } catch (error) {
      console.error(`Erro ao atualizar cliente com ID ${id}:`, error);
      throw error;
    }
  },

  delete: async (id) => {
    try {
      await axios.delete(`${API_URL}/${id}`);
      return true;
    } catch (error) {
      console.error(`Erro ao excluir cliente com ID ${id}:`, error);
      throw error;
    }
  }
};

export default clienteService;