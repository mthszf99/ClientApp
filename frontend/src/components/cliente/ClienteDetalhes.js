import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import clienteService from '../../services/clienteService';
import './ClienteDetalhes.css';

const ClienteDetalhes = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [cliente, setCliente] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCliente();
  }, [id]);

  const fetchCliente = async () => {
    try {
      setLoading(true);
      const data = await clienteService.getById(id);
      setCliente(data);
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os dados do cliente. Por favor, tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        await clienteService.delete(id);
        navigate('/clientes');
      } catch (err) {
        setError('Falha ao excluir o cliente. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!cliente) return <div className="error">Cliente não encontrado.</div>;

  return (
    <div className="cliente-detalhes-container">
      <div className="header">
        <h2>Detalhes do Cliente</h2>
        <div className="actions">
          <Link to="/clientes" className="btn-back">
            Voltar para Lista
          </Link>
          <Link to={`/clientes/editar/${id}`} className="btn-edit">
            Editar
          </Link>
          <button onClick={handleDelete} className="btn-delete">
            Excluir
          </button>
        </div>
      </div>

      <div className="cliente-card">
        <h3>{cliente.nome}</h3>
        <div className="cliente-info">
          <div className="info-section">
            <h4>Informações Gerais</h4>
            <div className="info-group">
              <span className="info-label">Tipo de Pessoa:</span>
              <span className="info-value">{cliente.tipoPessoa}</span>
            </div>
            
            {cliente.tipoPessoa === 'Físico' && cliente.cpf && (
              <div className="info-group">
                <span className="info-label">CPF:</span>
                <span className="info-value">{cliente.cpf}</span>
              </div>
            )}
            
            {cliente.tipoPessoa === 'Jurídico' && (
              <>
                {cliente.cnpj && (
                  <div className="info-group">
                    <span className="info-label">CNPJ:</span>
                    <span className="info-value">{cliente.cnpj}</span>
                  </div>
                )}
                
                {cliente.ie && (
                  <div className="info-group">
                    <span className="info-label">Inscrição Estadual:</span>
                    <span className="info-value">{cliente.ie}</span>
                  </div>
                )}
              </>
            )}
            
            {cliente.indicacao && (
              <div className="info-group">
                <span className="info-label">Indicação:</span>
                <span className="info-value">{cliente.indicacao}</span>
              </div>
            )}
          </div>
          
          <div className="info-section">
            <h4>Contato</h4>
            {cliente.endereco && (
              <div className="info-group">
                <span className="info-label">Endereço:</span>
                <span className="info-value">{cliente.endereco}</span>
              </div>
            )}
            
            {cliente.telefone && (
              <div className="info-group">
                <span className="info-label">Telefone:</span>
                <span className="info-value">{cliente.telefone}</span>
              </div>
            )}
            
            {cliente.email && (
              <div className="info-group">
                <span className="info-label">Email:</span>
                <span className="info-value">{cliente.email}</span>
              </div>
            )}
          </div>
        </div>
        
        {cliente.observacao && (
          <div className="observacao-section">
            <h4>Observações</h4>
            <p>{cliente.observacao}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ClienteDetalhes;