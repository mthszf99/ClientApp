import axios from 'axios';

const API_URL = 'http://localhost:8080/vendas';

const vendaService = {
  getAll: async () => {
    try {
      const response = await axios.get(API_URL, {
        // Adicione timeout e headers para mais informações
        timeout: 5000,
        headers: {
          'Accept': 'application/json'
        }
      }); 
      return response.data;
    } catch (error) {
      // Log detalhado do erro
      console.error('Erro completo:', error);
      console.error('Erro de resposta:', error.response);
      console.error('Erro de requisição:', error.request);
      console.error('Mensagem de erro:', error.message);
      
      throw error;
    }
  },

  getById: async (id) => {
    try {
      const response = await axios.get(`${API_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Erro ao buscar venda com ID ${id}:`, error);
      throw error;
    }
  },

  create: async (venda) => {
    try {
      const response = await axios.post(API_URL, venda);
      return response.data;
    } catch (error) {
      console.error('Erro ao criar venda:', error);
      throw error;
    }
  },

  update: async (id, venda) => {
    try {
      const response = await axios.put(`${API_URL}/${id}`, venda);
      return response.data;
    } catch (error) {
      console.error(`Erro ao atualizar venda com ID ${id}:`, error);
      throw error;
    }
  },

  delete: async (id) => {
    try {
      await axios.delete(`${API_URL}/${id}`);
      return true;
    } catch (error) {
      console.error(`Erro ao excluir venda com ID ${id}:`, error);
      throw error;
    }
  },

  addItem: async (vendaId, item) => {
    try {
      const response = await axios.post(`${API_URL}/${vendaId}/itens`, item);
      return response.data;
    } catch (error) {
      console.error(`Erro ao adicionar item à venda ${vendaId}:`, error);
      throw error;
    }
  },

  removeItem: async (vendaId, itemId) => {
    try {
      const response = await axios.delete(`${API_URL}/${vendaId}/itens/${itemId}`);
      return response.data;
    } catch (error) {
      console.error(`Erro ao remover item ${itemId} da venda ${vendaId}:`, error);
      throw error;
    }
  },

  finalizarVenda: async (id) => {
    try {
      const response = await axios.post(`${API_URL}/${id}/finalizar`);
      return response.data;
    } catch (error) {
      console.error(`Erro ao finalizar venda ${id}:`, error);
      throw error;
    }
  },

  downloadPdf: async (id) => {
    try {
      const response = await axios.get(`${API_URL}/${id}/pdf`, {
        responseType: 'blob'
      });
      return response.data;
    } catch (error) {
      console.error(`Erro ao baixar PDF da venda ${id}:`, error);
      throw error;
    }
  }
};

export default vendaService;