import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';

// CSS para o componente Sidebar
const Sidebar = () => {
  const location = useLocation();
  const [isOpen, setIsOpen] = useState(true);
  const [expandedMenus, setExpandedMenus] = useState({
    clientes: true,
    fornecedores: false,
    produtos: false,
    vendas: false
  });

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };

  const toggleSubmenu = (menu) => {
    setExpandedMenus({
      ...expandedMenus,
      [menu]: !expandedMenus[menu]
    });
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <>
      {/* Overlay para dispositivos móveis */}
      {isOpen && (
        <div 
          className="sidebar-overlay"
          onClick={toggleSidebar}
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            backgroundColor: 'rgba(0, 0, 0, 0.3)',
            zIndex: 10,
            display: 'none' // Escondido por padrão, visível apenas em dispositivos móveis via media query
          }}
        ></div>
      )}

      {/* Botão para abrir/fechar no mobile */}
      <button 
        className="sidebar-toggle"
        onClick={toggleSidebar}
        style={{
          position: 'fixed',
          left: isOpen ? '260px' : '20px',
          top: '20px',
          zIndex: 20,
          padding: '8px 12px',
          backgroundColor: '#4CAF50',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          transition: 'left 0.3s',
          display: 'none' // Escondido por padrão, visível apenas em dispositivos móveis via media query
        }}
      >
        {isOpen ? '←' : '→'}
      </button>

      {/* Sidebar */}
      <div
        className="sidebar"
        style={{
          width: '250px',
          height: '100vh',
          backgroundColor: '#333',
          color: 'white',
          position: 'fixed',
          left: isOpen ? '0' : '-250px',
          top: '0',
          transition: 'left 0.3s ease',
          zIndex: 15,
          boxShadow: '2px 0 5px rgba(0, 0, 0, 0.1)',
          display: 'flex',
          flexDirection: 'column'
        }}
      >
        {/* Logo/Cabeçalho */}
        <div
          className="sidebar-header"
          style={{
            padding: '20px',
            borderBottom: '1px solid #444',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}
        >
          <h3 style={{ margin: 0 }}>Sistema</h3>
          <button
            onClick={toggleSidebar}
            style={{
              background: 'transparent',
              border: 'none',
              color: 'white',
              fontSize: '20px',
              cursor: 'pointer',
              display: 'none' // Escondido por padrão, visível apenas em dispositivos móveis via media query
            }}
          >
            ×
          </button>
        </div>

        {/* Lista de Menus */}
        <div
          className="sidebar-menu"
          style={{
            display: 'flex',
            flexDirection: 'column',
            flex: 1,
            overflowY: 'auto'
          }}
        >
          {/* Cliente */}
          <div className="menu-item">
            <div
              className="menu-title"
              onClick={() => toggleSubmenu('clientes')}
              style={{
                padding: '15px 20px',
                borderBottom: '1px solid #444',
                cursor: 'pointer',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                backgroundColor: expandedMenus.clientes ? '#444' : 'transparent'
              }}
            >
              <span>Clientes</span>
              <span>{expandedMenus.clientes ? '▼' : '▶'}</span>
            </div>

            {expandedMenus.clientes && (
              <div className="submenu">
                <Link
                  to="/clientes"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/clientes') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Lista de Clientes
                </Link>
                <Link
                  to="/clientes/novo"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/clientes/novo') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Cadastrar Cliente
                </Link>
              </div>
            )}
          </div>

          {/* Fornecedor */}
          <div className="menu-item">
            <div
              className="menu-title"
              onClick={() => toggleSubmenu('fornecedores')}
              style={{
                padding: '15px 20px',
                borderBottom: '1px solid #444',
                cursor: 'pointer',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                backgroundColor: expandedMenus.fornecedores ? '#444' : 'transparent'
              }}
            >
              <span>Fornecedores</span>
              <span>{expandedMenus.fornecedores ? '▼' : '▶'}</span>
            </div>

            {expandedMenus.fornecedores && (
              <div className="submenu">
                <Link
                  to="/fornecedores"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/fornecedores') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Lista de Fornecedores
                </Link>
                <Link
                  to="/fornecedores/novo"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/fornecedores/novo') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Cadastrar Fornecedor
                </Link>
              </div>
            )}
          </div>

          {/* Produtos */}
          <div className="menu-item">
            <div
              className="menu-title"
              onClick={() => toggleSubmenu('produtos')}
              style={{
                padding: '15px 20px',
                borderBottom: '1px solid #444',
                cursor: 'pointer',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                backgroundColor: expandedMenus.produtos ? '#444' : 'transparent'
              }}
            >
              <span>Produtos</span>
              <span>{expandedMenus.produtos ? '▼' : '▶'}</span>
            </div>

            {expandedMenus.produtos && (
              <div className="submenu">
                <Link
                  to="/produtos/novo"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/produtos/novo') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Cadastrar Produto
                </Link>
                <Link
                  to="/produtos"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/produtos') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Lista de Produtos
                </Link>
              </div>
            )}
          </div>

          {/* Vendas */}
          <div className="menu-item">
            <div
              className="menu-title"
              onClick={() => toggleSubmenu('vendas')}
              style={{
                padding: '15px 20px',
                borderBottom: '1px solid #444',
                cursor: 'pointer',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                backgroundColor: expandedMenus.vendas ? '#444' : 'transparent'
              }}
            >
              <span>Vendas</span>
              <span>{expandedMenus.vendas ? '▼' : '▶'}</span>
            </div>

            {expandedMenus.vendas && (
              <div className="submenu">
                <Link
                  to="/vendas/nova"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/vendas/nova') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Criar Nova Venda
                </Link>
                <Link
                  to="/vendas"
                  style={{
                    padding: '12px 20px 12px 35px',
                    display: 'block',
                    color: 'white',
                    textDecoration: 'none',
                    borderBottom: '1px solid #444',
                    backgroundColor: isActive('/vendas') ? '#4CAF50' : 'transparent'
                  }}
                >
                  Lista de Vendas
                </Link>
              </div>
            )}
          </div>

          {/* Orçamento (sem submenu) */}
          <Link
            to="/orcamento"
            style={{
              padding: '15px 20px',
              display: 'block',
              color: 'white',
              textDecoration: 'none',
              borderBottom: '1px solid #444',
              backgroundColor: isActive('/orcamento') ? '#4CAF50' : 'transparent'
            }}
          >
            Orçamento
          </Link>
        </div>
      </div>
    </>
  );
};

export default Sidebar;