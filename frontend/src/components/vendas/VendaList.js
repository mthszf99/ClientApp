import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import vendaService from '../../services/vendaService';
import './VendaList.css';

const VendaList = () => {
  const [vendas, setVendas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchVendas();
  }, []);

  const fetchVendas = async () => {
    try {
      setLoading(true);
      const data = await vendaService.getAll();
      setVendas(data);
      setError(null);
    } catch (err) {
      console.error('Erro Completo:', err);
      setError('Falha ao carregar as vendas. Por favor, tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir esta venda?')) {
      try {
        await vendaService.delete(id);
        setVendas(vendas.filter(venda => venda.id !== id));
      } catch (err) {
        setError('Falha ao excluir a venda. Por favor, tente novamente.');
        console.error(err);
      }
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="venda-list-container">
      <div className="header">
        <h2>Vendas</h2>
        <Link to="/vendas/novo" className="btn-novo">Nova Venda</Link>
      </div>
      
      {vendas.length === 0 ? (
        <div className="no-records">Nenhuma venda cadastrada.</div>
      ) : (
        <div className="table-responsive">
          <table className="venda-table">
            <thead>
              <tr>
                <th>Código</th>
                <th>Cliente</th>
                <th>Data</th>
                <th>Valor Total</th>
                <th>Forma de Pagamento</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {vendas.map(venda => (
                <tr key={venda.id}>
                  <td>{venda.id}</td>
                  <td>{venda.cliente.nome}</td>
                  <td>{formatDate(venda.dataVenda)}</td>
                  <td>{formatCurrency(venda.valorTotal)}</td>
                  <td>{venda.formaPagamento || 'Não especificado'}</td>
                  <td className="actions">
                    <Link to={`/vendas/visualizar/${venda.id}`} className="btn-view">
                      Visualizar
                    </Link>
                    <Link to={`/vendas/editar/${venda.id}`} className="btn-edit">
                      Editar
                    </Link>
                    <button 
                      onClick={() => handleDelete(venda.id)} 
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

export default VendaList