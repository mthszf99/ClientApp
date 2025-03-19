import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';

// Importação dos componentes de cliente
import ClienteList from './components/cliente/ClienteList';
import ClienteForm from './components/cliente/ClienteForm';
import ClienteDetalhes from './components/cliente/ClienteDetalhes';

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
          <Route path="/fornecedores" element={<div className="placeholder">Lista de Fornecedores</div>} />
          <Route path="/fornecedores/novo" element={<div className="placeholder">Cadastro de Fornecedores</div>} />
          
          {/* Rotas para Produtos (ainda precisam ser implementadas) */}
          <Route path="/produtos" element={<div className="placeholder">Lista de Produtos</div>} />
          <Route path="/produtos/novo" element={<div className="placeholder">Cadastro de Produtos</div>} />
          
          {/* Rotas para Vendas (ainda precisam ser implementadas) */}
          <Route path="/vendas" element={<div className="placeholder">Lista de Vendas</div>} />
          <Route path="/vendas/nova" element={<div className="placeholder">Nova Venda</div>} />
          
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
