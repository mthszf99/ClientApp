import axios from 'axios';

const API_URL = 'http://localhost:8080/orcamentos';

const orcamentoService = {
  getAll: async () => {
    try {
      const response = await axios.get(API_URL);
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar orçamentos:', error);
      throw error;
    }
  },

  getById: async (id) => {
    try {
      const response = await axios.get(`${API_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Erro ao buscar orçamento com ID ${id}:`, error);
      throw error;
    }
  },

  create: async (orcamento) => {
    try {
      const response = await axios.post(API_URL, orcamento);
      return response.data;
    } catch (error) {
      console.error('Erro ao criar orçamento:', error);
      throw error;
    }
  },

  update: async (id, orcamento) => {
    try {
      const response = await axios.put(`${API_URL}/${id}`, orcamento);
      return response.data;
    } catch (error) {
      console.error(`Erro ao atualizar orçamento com ID ${id}:`, error);
      throw error;
    }
  },

  delete: async (id) => {
    try {
      await axios.delete(`${API_URL}/${id}`);
      return true;
    } catch (error) {
      console.error(`Erro ao excluir orçamento com ID ${id}:`, error);
      throw error;
    }
  },

  gerarPdf: async (orcamento) => {
    try {
      const response = await axios.post(`${API_URL}/gerar-pdf`, orcamento, {
        responseType: 'blob'
      });
      return response.data;
    } catch (error) {
      console.error('Erro ao gerar PDF do orçamento:', error);
      throw error;
    }
  }
};

export default orcamentoService;