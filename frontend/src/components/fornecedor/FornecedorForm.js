import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import fornecedorService from '../../services/fornecedorService';
import './FornecedorForm.css';

const FornecedorForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;
  
  const [formData, setFormData] = useState({
    nome: '',
    cpfCnpj: '',
    endereco: '',
    tipo: 'Físico',
    telefone: '',
    email: ''
  });
  
  const [loading, setLoading] = useState(isEditing);
  const [error, setError] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    if (isEditing) {
      fetchFornecedor();
    }
  }, [id]);

  const fetchFornecedor = async () => {
    try {
      setLoading(true);
      const fornecedor = await fornecedorService.getById(id);
      setFormData(fornecedor);
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os dados do fornecedor. Por favor, tente novamente.');
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
    
    if (!formData.cpfCnpj.trim()) {
      setError('CPF/CNPJ é obrigatório.');
      return false;
    }
    
    if (formData.email.trim() && !validarEmail(formData.email)) {
      setError('Email inválido.');
      return false;
    }
    
    return true;
  };

  const validarCPF = (cpf) => {
    // Implementação simplificada - em produção, use uma validação mais robusta
    const cpfLimpo = cpf.replace(/[^\d]/g, '');
    return cpfLimpo.length === 11;
  };

  const validarCNPJ = (cnpj) => {
    // Implementação simplificada - em produção, use uma validação mais robusta
    const cnpjLimpo = cnpj.replace(/[^\d]/g, '');
    return cnpjLimpo.length === 14;
  };

  const validarEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitted(true);
    setError(null);
    
    if (!validateForm()) return;
    
    try {
      setLoading(true);
      if (isEditing) {
        await fornecedorService.update(id, formData);
      } else {
        await fornecedorService.create(formData);
      }
      navigate('/fornecedores');
    } catch (err) {
      setError(`Falha ao ${isEditing ? 'atualizar' : 'criar'} o fornecedor. Por favor, tente novamente.`);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/fornecedores');
  };

  if (loading && isEditing) return <div className="loading">Carregando...</div>;

  return (
    <div className="cliente-form-container">
      <h2>{isEditing ? 'Editar Fornecedor' : 'Novo Fornecedor'}</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="cliente-form">
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
          <label htmlFor="cpfCnpj">CPF/CNPJ*</label>
          <input
            type="text"
            id="cpfCnpj"
            name="cpfCnpj"
            value={formData.cpfCnpj}
            onChange={handleChange}
            className={submitted && !formData.cpfCnpj.trim() ? 'invalid' : ''}
            required
          />
          {submitted && !formData.cpfCnpj.trim() && (
            <div className="error-feedback">CPF/CNPJ é obrigatório</div>
          )}
        </div>
        
        <div className="form-group">
          <label htmlFor="endereco">Endereço</label>
          <input
            type="text"
            id="endereco"
            name="endereco"
            value={formData.endereco}
            onChange={handleChange}
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="telefone">Telefone</label>
          <input
            type="text"
            id="telefone"
            name="telefone"
            value={formData.telefone}
            onChange={handleChange}
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className={submitted && formData.email && !validarEmail(formData.email) ? 'invalid' : ''}
          />
          {submitted && formData.email && !validarEmail(formData.email) && (
            <div className="error-feedback">Email inválido</div>
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

export default FornecedorForm;