import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import produtoService from '../../services/produtoService';
import './ProdutoDetalhes.css';

const ProdutoDetalhes = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [produto, setProduto] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchProduto();
  }, [id]);

  const fetchProduto = async () => {
    try {
      setLoading(true);
      const data = await produtoService.getById(id);
      setProduto(data);
      setError(null);
    } catch (err) {
      setError('Falha ao carregar os dados do produto. Por favor, tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Tem certeza que deseja excluir este produto?')) {
      try {
        await produtoService.delete(id);
        navigate('/produtos');
      } catch (err) {
        setError('Falha ao excluir o produto. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!produto) return <div className="error">Produto não encontrado.</div>;

  return (
    <div className="produto-detalhes-container">
      <div className="header">
        <h2>Detalhes do Produto</h2>
        <div className="actions">
          <Link to="/produtos" className="btn-back">
            Voltar para Lista
          </Link>
          <Link to={`/produtos/editar/${id}`} className="btn-edit">
            Editar
          </Link>
          <button onClick={handleDelete} className="btn-delete">
            Excluir
          </button>
        </div>
      </div>

      <div className="produto-card">
        <h3>{produto.nome}</h3>
        <div className="produto-info">
          <div className="info-section">
            <h4>Informações Gerais</h4>
            <div className="info-group">
              <span className="info-label">Marca:</span>
              <span className="info-value">{produto.marca}</span>
            </div>
            
            <div className="info-group">
              <span className="info-label">Quantidade em Estoque:</span>
              <span className="info-value">{produto.quantidade}</span>
            </div>
            
            {produto.codigo && (
              <div className="info-group">
                <span className="info-label">Código:</span>
                <span className="info-value">{produto.codigo}</span>
              </div>
            )}
            
            {produto.tipo && (
              <div className="info-group">
                <span className="info-label">Tipo:</span>
                <span className="info-value">{produto.tipo}</span>
              </div>
            )}
          </div>
          
          <div className="info-section">
            <h4>Valores</h4>
            <div className="info-group">
              <span className="info-label">Valor de Compra:</span>
              <span className="info-value">R$ {produto.valorCompra.toFixed(2)}</span>
            </div>
            
            <div className="info-group">
              <span className="info-label">Valor de Venda:</span>
              <span className="info-value">R$ {produto.valorVenda.toFixed(2)}</span>
            </div>
          </div>
        </div>
        
        {produto.descricao && (
          <div className="descricao-section">
            <h4>Descrição</h4>
            <p>{produto.descricao}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProdutoDetalhes;