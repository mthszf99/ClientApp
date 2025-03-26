import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import vendaService from '../../services/vendaService';
import clienteService from '../../services/clienteService';
import produtoService from '../../services/produtoService';
import './VendaForm.css';

const VendaForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;
  
  const [formData, setFormData] = useState({
    cliente: null,
    dataVenda: null,
    valorTotal: 0,
    desconto: 0,
    formaPagamento: '',
    observacoes: '',
    itens: []
  });
  
  const [clientes, setClientes] = useState([]);
  const [produtos, setProdutos] = useState([]);
  const [clienteSelecionado, setClienteSelecionado] = useState(null);
  const [produtoSelecionado, setProdutoSelecionado] = useState(null);
  const [quantidadeProduto, setQuantidadeProduto] = useState(1);
  
  const [loading, setLoading] = useState(isEditing);
  const [error, setError] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    fetchInitialData();
  }, []);

  useEffect(() => {
    if (isEditing) {
      fetchVenda();
    }
  }, [id]);

  const fetchInitialData = async () => {
    try {
      const [clientesData, produtosData] = await Promise.all([
        clienteService.getAll(),
        produtoService.getAll()
      ]);
      setClientes(clientesData);
      setProdutos(produtosData);
    } catch (err) {
      setError('Falha ao carregar dados iniciais.');
      console.error(err);
    }
  };

  const fetchVenda = async () => {
    try {
      setLoading(true);
      const venda = await vendaService.getById(id);
      setFormData(venda);
      setClienteSelecionado(venda.cliente);
    } catch (err) {
      setError('Falha ao carregar os dados da venda.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAdicionarItem = () => {
    if (!produtoSelecionado || quantidadeProduto <= 0) {
      setError('Selecione um produto e insira uma quantidade válida.');
      return;
    }

    const novoItem = {
      produto: produtoSelecionado,
      quantidade: quantidadeProduto,
      valorUnitario: produtoSelecionado.valorVenda
    };

    setFormData(prev => ({
      ...prev,
      itens: [...prev.itens, novoItem]
    }));

    // Resetar seleção de produto
    setProdutoSelecionado(null);
    setQuantidadeProduto(1);
  };

  const handleRemoverItem = (index) => {
    setFormData(prev => ({
      ...prev,
      itens: prev.itens.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitted(true);
    setError(null);

    // Validações básicas
    if (!clienteSelecionado) {
      setError('Selecione um cliente.');
      return;
    }

    if (formData.itens.length === 0) {
      setError('Adicione pelo menos um item à venda.');
      return;
    }

    try {
      setLoading(true);
      const vendaData = {
        ...formData,
        cliente: clienteSelecionado,
        dataVenda: new Date()
      };

      if (isEditing) {
        await vendaService.update(id, vendaData);
      } else {
        await vendaService.create(vendaData);
      }
      
      navigate('/vendas');
    } catch (err) {
      setError(`Falha ao ${isEditing ? 'atualizar' : 'criar'} a venda.`);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const calcularTotal = () => {
    return formData.itens.reduce((total, item) => 
      total + (item.quantidade * item.valorUnitario), 0
    ) - (formData.desconto || 0);
  };

  if (loading) return <div className="loading">Carregando...</div>;

  return (
    <div className="venda-form-container">
      <h2>{isEditing ? 'Editar Venda' : 'Nova Venda'}</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="venda-form">
        <div className="form-group">
          <label>Cliente*</label>
          <select 
            value={clienteSelecionado?.id || ''}
            onChange={(e) => {
              const cliente = clientes.find(c => c.id === Number(e.target.value));
              setClienteSelecionado(cliente);
            }}
            required
          >
            <option value="">Selecione um cliente</option>
            {clientes.map(cliente => (
              <option key={cliente.id} value={cliente.id}>
                {cliente.nome}
              </option>
            ))}
          </select>
        </div>

        <div className="itens-venda-section">
          <h3>Itens da Venda</h3>
          <div className="adicionar-item">
            <select 
              value={produtoSelecionado?.id || ''}
              onChange={(e) => {
                const produto = produtos.find(p => p.id === Number(e.target.value));
                setProdutoSelecionado(produto);
              }}
            >
              <option value="">Selecione um produto</option>
              {produtos.map(produto => (
                <option key={produto.id} value={produto.id}>
                  {produto.nome} - {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(produto.valorVenda)}
                </option>
              ))}
            </select>
            <input 
              type="number" 
              min="1" 
              value={quantidadeProduto} 
              onChange={(e) => setQuantidadeProduto(Number(e.target.value))}
            />
            <button type="button" onClick={handleAdicionarItem}>
              Adicionar Item
            </button>
          </div>

          <table className="itens-table">
            <thead>
              <tr>
                <th>Produto</th>
                <th>Quantidade</th>
                <th>Valor Unitário</th>
                <th>Subtotal</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {formData.itens.map((item, index) => (
                <tr key={index}>
                  <td>{item.produto.nome}</td>
                  <td>{item.quantidade}</td>
                  <td>{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.valorUnitario)}</td>
                  <td>{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.quantidade * item.valorUnitario)}</td>
                  <td>
                    <button type="button" onClick={() => handleRemoverItem(index)}>
                      Remover
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="form-group">
          <label>Desconto</label>
          <input 
            type="number" 
            value={formData.desconto || 0} 
            onChange={(e) => setFormData(prev => ({
              ...prev, 
              desconto: Number(e.target.value)
            }))}
            step="0.01" 
            min="0"
          />
        </div>

        <div className="form-group">
          <label>Forma de Pagamento</label>
          <select 
            value={formData.formaPagamento || ''} 
            onChange={(e) => setFormData(prev => ({
              ...prev, 
              formaPagamento: e.target.value
            }))}
          >
            <option value="">Selecione</option>
            <option value="Dinheiro">Dinheiro</option>
            <option value="Cartão de Crédito">Cartão de Crédito</option>
            <option value="Cartão de Débito">Cartão de Débito</option>
            <option value="Pix">Pix</option>
          </select>
        </div>

        <div className="form-group">
          <label>Observações</label>
          <textarea 
            value={formData.observacoes || ''} 
            onChange={(e) => setFormData(prev => ({
              ...prev, 
              observacoes: e.target.value
            }))}
          />
        </div>

        <div className="total-venda">
          <strong>Total da Venda: </strong>
          {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(calcularTotal())}
        </div>

        <div className="form-actions">
          <button 
            type="button" 
            onClick={() => navigate('/vendas')} 
            className="btn-cancel"
          >
            Cancelar
          </button>
          <button 
            type="submit" 
            className="btn-submit" 
            disabled={loading}
          >
            {loading ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default VendaForm;