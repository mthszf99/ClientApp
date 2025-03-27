import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import fornecedorService from '../../services/fornecedorService';
import './FornecedorDetalhes.css';

const FornecedorDetalhes = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [fornecedor, setFornecedor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchFornecedor();
  }, [id]);

  const fetchFornecedor = async () => {
    try {
      setLoading(true);
      const data = await fornecedorService.getById(id);
      setFornecedor(data);
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os dados do fornecedor. Por favor, tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Tem certeza que deseja excluir este fornecedor?')) {
      try {
        await fornecedorService.delete(id);
        navigate('/fornecedores');
      } catch (err) {
        setError('Falha ao excluir o fornecedor. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!fornecedor) return <div className="error">Fornecedor não encontrado.</div>;

  return (
    <div className="cliente-detalhes-container">
      <div className="header">
        <h2>Detalhes do Fornecedor</h2>
        <div className="actions">
          <Link to="/fornecedores" className="btn-back">
            Voltar para Lista
          </Link>
          <Link to={`/fornecedores/editar/${id}`} className="btn-edit">
            Editar
          </Link>
          <button onClick={handleDelete} className="btn-delete">
            Excluir
          </button>
        </div>
      </div>

      <div className="cliente-card">
        <h3>{fornecedor.nome}</h3>
        <div className="cliente-info">
          <div className="info-section">
            <h4>Informações Gerais</h4>
            <div className="info-group">
              <span className="info-label">CPF/CNPJ:</span>
              <span className="info-value">{fornecedor.cpfCnpj}</span>
            </div>
          </div>
          
          <div className="info-section">
            <h4>Contato</h4>
            {fornecedor.endereco && (
              <div className="info-group">
                <span className="info-label">Endereço:</span>
                <span className="info-value">{fornecedor.endereco}</span>
              </div>
            )}
            
            {fornecedor.telefone && (
              <div className="info-group">
                <span className="info-label">Telefone:</span>
                <span className="info-value">{fornecedor.telefone}</span>
              </div>
            )}
            
            {fornecedor.email && (
              <div className="info-group">
                <span className="info-label">Email:</span>
                <span className="info-value">{fornecedor.email}</span>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default FornecedorDetalhes;