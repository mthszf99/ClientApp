import axios from 'axios';

const API_URL = 'http://localhost:8080/fornecedores';

const fornecedorService = {
  getAll: async () => {
    try {
      console.log('Fazendo requisição para:', API_URL);
      const response = await axios.get(API_URL);
      console.log('Dados recebidos:', response.data);
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar fornecedores:', error);
      throw error;
    }
  },  

  getById: async (id) => {
    try {
      const response = await axios.get(`${API_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Erro ao buscar fornecedor com ID ${id}:`, error);
      throw error;
    }
  },

  create: async (fornecedor) => {
    try {
      const response = await axios.post(API_URL, fornecedor);
      return response.data;
    } catch (error) {
      console.error('Erro ao criar fornecedor:', error);
      throw error;
    }
  },

  update: async (id, fornecedor) => {
    try {
      const response = await axios.put(`${API_URL}/${id}`, fornecedor);
      return response.data;
    } catch (error) {
      console.error(`Erro ao atualizar fornecedor com ID ${id}:`, error);
      throw error;
    }
  },

  delete: async (id) => {
    try {
      await axios.delete(`${API_URL}/${id}`);
      return true;
    } catch (error) {
      console.error(`Erro ao excluir fornecedor com ID ${id}:`, error);
      throw error;
    }
  }
};

export default fornecedorService;