import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import produtoService from '../../services/produtoService';
import './ProdutoList.css';

const ProdutoList = () => {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchProdutos();
  }, []);

  const fetchProdutos = async () => {
    try {
      setLoading(true);
      const data = await produtoService.getAll();
      setProdutos(data);
      setError(null);
    } catch (err) {
      console.error('Erro completo:', err);
      setError(`Falha ao carregar os produtos: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este produto?')) {
      try {
        await produtoService.delete(id);
        setProdutos(produtos.filter(produto => produto.id !== id));
      } catch (err) {
        setError('Falha ao excluir o produto. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="produto-list-container">
      <div className="header">
        <h2>Produtos</h2>
        <Link to="/produtos/novo" className="btn-novo">Novo Produto</Link>
      </div>
      
      {produtos.length === 0 ? (
        <div className="no-records">Nenhum produto cadastrado.</div>
      ) : (
        <div className="table-responsive">
          <table className="produto-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Marca</th>
                <th>Quantidade</th>
                <th>Valor de Venda</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {produtos.map(produto => (
                <tr key={produto.id}>
                  <td>{produto.nome}</td>
                  <td>{produto.marca}</td>
                  <td>{produto.quantidade}</td>
                  <td>R$ {produto.valorVenda.toFixed(2)}</td>
                  <td className="actions">
                    <Link to={`/produtos/visualizar/${produto.id}`} className="btn-view">
                      Visualizar
                    </Link>
                    <Link to={`/produtos/editar/${produto.id}`} className="btn-edit">
                      Editar
                    </Link>
                    <button 
                      onClick={() => handleDelete(produto.id)} 
                      className="btn-delete"
                    >
                      Excluir
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ProdutoList;