
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import produtoService from '../../services/produtoService';
import fornecedorService from '../../services/fornecedorService';
import './ProdutoForm.css';

const ProdutoForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;
  
  const [fornecedores, setFornecedores] = useState([]); // Add this line to declare fornecedores state
  
  const [formData, setFormData] = useState({
    nome: '',
    quantidade: '',
    marca: '',
    valorCompra: '',
    valorVenda: '',
    codigo: '',
    tipo: '',
    descricao: '',
    fornecedor: null
  });
  
  const [loading, setLoading] = useState(isEditing);
  const [error, setError] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    fetchFornecedores();

    if (isEditing) {
      fetchProduto();
    }
  }, [id]);

  const fetchFornecedores = async () => {
    try {
      const data = await fornecedorService.getAll();
      setFornecedores(data);
    } catch (err) {
      console.error('Erro ao buscar fornecedores:', err);
      setError('Falha ao carregar fornecedores');
    }
  };

  const fetchProduto = async () => {
    try {
      setLoading(true);
      const produto = await produtoService.getById(id);
      setFormData({
        ...produto,
        valorCompra: produto.valorCompra.toFixed(2),
        valorVenda: produto.valorVenda.toFixed(2),
        fornecedor: produto.fornecedor ? produto.fornecedor.id : null
      });
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os dados do produto. Por favor, tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevData => ({
      ...prevData,
      [name]: value
    }));
  };

  const validateForm = () => {
    // Validação básica
    if (!formData.nome.trim()) {
      setError('Nome é obrigatório.');
      return false;
    }
    
    if (!formData.quantidade || isNaN(formData.quantidade)) {
      setError('Quantidade inválida.');
      return false;
    }
    
    if (!formData.valorCompra || isNaN(formData.valorCompra)) {
      setError('Valor de Compra inválido.');
      return false;
    }
    
    if (!formData.valorVenda || isNaN(formData.valorVenda)) {
      setError('Valor de Venda inválido.');
      return false;
    }
    
    if (!formData.fornecedor) {
      setError('Fornecedor é obrigatório.');
      return false;
    }
    
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitted(true);
    setError(null);
    
    if (!validateForm()) return;
    
    try {
      setLoading(true);
      // Converter valores para números e preparar objeto
      const produtoData = {
        ...formData,
        quantidade: parseInt(formData.quantidade),
        valorCompra: parseFloat(formData.valorCompra),
        valorVenda: parseFloat(formData.valorVenda),
        fornecedor: { id: formData.fornecedor }
      };

      if (isEditing) {
        await produtoService.update(id, produtoData);
      } else {
        await produtoService.create(produtoData);
      }
      navigate('/produtos');
    } catch (err) {
      setError(`Falha ao ${isEditing ? 'atualizar' : 'criar'} o produto. Por favor, tente novamente.`);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/produtos');
  };

  if (loading && isEditing) return <div className="loading">Carregando...</div>;

  return (
    <div className="produto-form-container">
      <h2>{isEditing ? 'Editar Produto' : 'Novo Produto'}</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="produto-form">
        <div className="form-group">
          <label htmlFor="nome">Nome*</label>
          <input
            type="text"
            id="nome"
            name="nome"
            value={formData.nome}
            onChange={handleChange}
            className={submitted && !formData.nome.trim() ? 'invalid' : ''}
            required
          />
          {submitted && !formData.nome.trim() && (
            <div className="error-feedback">Nome é obrigatório</div>
          )}
        </div>
        
        <div className="form-group">
          <label htmlFor="marca">Marca*</label>
          <input
            type="text"
            id="marca"
            name="marca"
            value={formData.marca}
            onChange={handleChange}
            className={submitted && !formData.marca.trim() ? 'invalid' : ''}
            required
          />
          {submitted && !formData.marca.trim() && (
            <div className="error-feedback">Marca é obrigatória</div>
          )}
        </div>
        
        <div className="form-group">
          <label htmlFor="quantidade">Quantidade*</label>
          <input
            type="number"
            id="quantidade"
            name="quantidade"
            value={formData.quantidade}
            onChange={handleChange}
            className={submitted && (!formData.quantidade || isNaN(formData.quantidade)) ? 'invalid' : ''}
            required
          />
          {submitted && (!formData.quantidade || isNaN(formData.quantidade)) && (
            <div className="error-feedback">Quantidade inválida</div>
          )}
        </div>
        
        <div className="form-group">
          <label htmlFor="valorCompra">Valor de Compra*</label>
          <input
            type="number"
            id="valorCompra"
            name="valorCompra"
            value={formData.valorCompra}
            onChange={handleChange}
            step="0.01"
            className={submitted && (!formData.valorCompra || isNaN(formData.valorCompra)) ? 'invalid' : ''}
            required
          />
          {submitted && (!formData.valorCompra || isNaN(formData.valorCompra)) && (
            <div className="error-feedback">Valor de Compra inválido</div>
          )}
        </div>
        
        <div className="form-group">
          <label htmlFor="valorVenda">Valor de Venda*</label>
          <input
            type="number"
            id="valorVenda"
            name="valorVenda"
            value={formData.valorVenda}
            onChange={handleChange}
            step="0.01"
            className={submitted && (!formData.valorVenda || isNaN(formData.valorVenda)) ? 'invalid' : ''}
            required
          />
          {submitted && (!formData.valorVenda || isNaN(formData.valorVenda)) && (
            <div className="error-feedback">Valor de Venda inválido</div>
          )}
        </div>
        
        <div className="form-group">
          <label htmlFor="codigo">Código</label>
          <input
            type="text"
            id="codigo"
            name="codigo"
            value={formData.codigo}
            onChange={handleChange}
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="tipo">Tipo</label>
          <input
            type="text"
            id="tipo"
            name="tipo"
            value={formData.tipo}
            onChange={handleChange}
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="fornecedor">Fornecedor*</label>
          <select
            id="fornecedor"
            name="fornecedor"
            value={formData.fornecedor || ''}
            onChange={handleChange}
            className={submitted && !formData.fornecedor ? 'invalid' : ''}
            required
          >
            <option value="">Selecione um Fornecedor</option>
            {fornecedores.map(fornecedor => (
              <option key={fornecedor.id} value={fornecedor.id}>
                {fornecedor.nome}
              </option>
            ))}
          </select>
          {submitted && !formData.fornecedor && (
            <div className="error-feedback">Fornecedor é obrigatório</div>
          )}
        </div>
        
        <div className="form-actions">
          <button type="button" onClick={handleCancel} className="btn-cancel">
            Cancelar
          </button>
          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProdutoForm;