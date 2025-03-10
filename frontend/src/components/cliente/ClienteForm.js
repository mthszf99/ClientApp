import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import clienteService from '../../services/clienteService';
import './ClienteForm.css';

const ClienteForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;
  
  const [formData, setFormData] = useState({
    nome: '',
    endereco: '',
    tipoPessoa: 'Físico',
    indicacao: '',
    cpf: '',
    cnpj: '',
    ie: '',
    telefone: '',
    email: '',
    observacao: ''
  });
  
  const [loading, setLoading] = useState(isEditing);
  const [error, setError] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    if (isEditing) {
      fetchCliente();
    }
  }, [id]);

  const fetchCliente = async () => {
    try {
      setLoading(true);
      const cliente = await clienteService.getById(id);
      setFormData(cliente);
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os dados do cliente. Por favor, tente novamente.');
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

  const handleTipoPessoaChange = (e) => {
    const tipoPessoa = e.target.value;
    setFormData(prevData => ({
      ...prevData,
      tipoPessoa,
      // Limpa campos específicos ao trocar tipo de pessoa
      cpf: tipoPessoa === 'Jurídico' ? '' : prevData.cpf,
      cnpj: tipoPessoa === 'Físico' ? '' : prevData.cnpj,
      ie: tipoPessoa === 'Físico' ? '' : prevData.ie
    }));
  };

  const validateForm = () => {
    // Validação básica
    if (!formData.nome.trim()) {
      setError('Nome é obrigatório.');
      return false;
    }
    
    if (formData.tipoPessoa === 'Físico' && formData.cpf.trim() && !validarCPF(formData.cpf)) {
      setError('CPF inválido.');
      return false;
    }
    
    if (formData.tipoPessoa === 'Jurídico' && formData.cnpj.trim() && !validarCNPJ(formData.cnpj)) {
      setError('CNPJ inválido.');
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
        await clienteService.update(id, formData);
      } else {
        await clienteService.create(formData);
      }
      navigate('/clientes');
    } catch (err) {
      setError(`Falha ao ${isEditing ? 'atualizar' : 'criar'} o cliente. Por favor, tente novamente.`);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/clientes');
  };

  if (loading && isEditing) return <div className="loading">Carregando...</div>;

  return (
    <div className="cliente-form-container">
      <h2>{isEditing ? 'Editar Cliente' : 'Novo Cliente'}</h2>
      
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
          <label htmlFor="tipoPessoa">Tipo de Pessoa</label>
          <select
            id="tipoPessoa"
            name="tipoPessoa"
            value={formData.tipoPessoa}
            onChange={handleTipoPessoaChange}
          >
            <option value="Físico">Pessoa Física</option>
            <option value="Jurídico">Pessoa Jurídica</option>
          </select>
        </div>
        
        {formData.tipoPessoa === 'Físico' && (
          <div className="form-group">
            <label htmlFor="cpf">CPF</label>
            <input
              type="text"
              id="cpf"
              name="cpf"
              value={formData.cpf}
              onChange={handleChange}
              className={submitted && formData.cpf && !validarCPF(formData.cpf) ? 'invalid' : ''}
            />
            {submitted && formData.cpf && !validarCPF(formData.cpf) && (
              <div className="error-feedback">CPF inválido</div>
            )}
          </div>
        )}
        
        {formData.tipoPessoa === 'Jurídico' && (
          <>
            <div className="form-group">
              <label htmlFor="cnpj">CNPJ</label>
              <input
                type="text"
                id="cnpj"
                name="cnpj"
                value={formData.cnpj}
                onChange={handleChange}
                className={submitted && formData.cnpj && !validarCNPJ(formData.cnpj) ? 'invalid' : ''}
              />
              {submitted && formData.cnpj && !validarCNPJ(formData.cnpj) && (
                <div className="error-feedback">CNPJ inválido</div>
              )}
            </div>
            
            <div className="form-group">
              <label htmlFor="ie">Inscrição Estadual</label>
              <input
                type="text"
                id="ie"
                name="ie"
                value={formData.ie}
                onChange={handleChange}
              />
            </div>
          </>
        )}
        
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
        
        <div className="form-group">
          <label htmlFor="indicacao">Indicação</label>
          <input
            type="text"
            id="indicacao"
            name="indicacao"
            value={formData.indicacao}
            onChange={handleChange}
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="observacao">Observações</label>
          <textarea
            id="observacao"
            name="observacao"
            value={formData.observacao}
            onChange={handleChange}
            rows={4}
          />
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

export default ClienteForm;