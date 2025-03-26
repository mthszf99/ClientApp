import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';

// Importação dos componentes de cliente
import ClienteList from './components/cliente/ClienteList';
import ClienteForm from './components/cliente/ClienteForm';
import ClienteDetalhes from './components/cliente/ClienteDetalhes';
import ProdutoList from './components/produtos/ProdutoList';
import ProdutoForm from './components/produtos/ProdutoForm';
import ProdutoDetalhes from './components/produtos/ProdutoDetalhes';
import VendaList from './components/vendas/VendaList';
import VendaDetalhes from './components/vendas/VendaDetalhes';
import VendaForm from './components/vendas/VendaForm';

// Importação do componente Sidebar
import Sidebar from './components/layout/Sidebar';

// Layout Principal
const Layout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <div className="app-container">
      <Sidebar />
      <main className={`main-content ${sidebarOpen ? 'sidebar-open' : ''}`}>
        {children}
      </main>
      <footer className="footer">
        <p>&copy; {new Date().getFullYear()} - Sistema de Gerenciamento</p>
      </footer>
    </div>
  );
};

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          {/* Redireciona a rota raiz para a página de clientes */}
          <Route path="/" element={<Navigate to="/clientes" />} />
          
          {/* Rotas de Clientes */}
          <Route path="/clientes" element={<ClienteList />} />
          <Route path="/clientes/novo" element={<ClienteForm />} />
          <Route path="/clientes/editar/:id" element={<ClienteForm />} />
          <Route path="/clientes/visualizar/:id" element={<ClienteDetalhes />} />
          
          {/* Rotas para Fornecedores (ainda precisam ser implementadas) */}
          <Route path="/fornecedores" element={<ClienteList />} />
          <Route path="/fornecedores/novo" element={<div className="placeholder">Cadastro de Fornecedores</div>} />
          
          {/* Rotas para Produtos (ainda precisam ser implementadas) */}
          <Route path="/produtos" element={<ProdutoList />} />
          <Route path="/produtos/novo" element={<ProdutoForm/>} />
          <Route path="/produtos/editar/:id" element={<ProdutoForm />} />
          <Route path="/produtos/visualizar/:id" element={<ProdutoDetalhes />} />
          
          {/* Rotas para Vendas (ainda precisam ser implementadas) */}
          <Route path="/vendas" element={<VendaList />} />
          <Route path="/vendas/nova" element={<VendaForm />} />
          <Route path="/vendas/visualizar/:id" element={<VendaDetalhes />} />
          
          {/* Rota para Orçamento (ainda precisa ser implementada) */}
          <Route path="/orcamento" element={<div className="placeholder">Orçamentos</div>} />
          
          {/* Rota para páginas não encontradas */}
          <Route path="*" element={<div className="not-found">Página não encontrada</div>} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
