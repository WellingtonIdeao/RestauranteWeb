package service;

import dao.ItemPedidoDAO;
import dao.TradicionalDAO;
import model.ItemPedido;
import model.Tradicional;

public class TradicionalService extends AbstractService<Tradicional> {

	public TradicionalService() {
		this.dao = new TradicionalDAO();
	}

	@Override
	public void inserir(Tradicional t) {
		manager = fac.createEntityManager();

		try {
			dao.setManager(manager);
			ItemPedidoDAO ipdao = new ItemPedidoDAO();
			ipdao.setManager(manager);
			
			// se pedido for nulo
			if (t == null) {
				throw new Exception("Entidade passada para inser��o � nula");
			}
			// se n�o existir mesa
			if (t.getMesa() == null)
				throw new Exception("Pedido Tradicional sem mesa");

			// se n�o tiver item no Pedido
			if (t.getItens().isEmpty())
				throw new Exception("Pedido Tradicional sem itens");

			// inserindo os itens com produtos no BD
			for (ItemPedido i : t.getItens()) {
				if (i.getProduto() == null)
					throw new Exception("Item pedido sem produto");
				i.setPedido(t);
				ipdao.inserir(i);

			}

			dao.inserir(t);
			manager.getTransaction().begin();
			manager.getTransaction().commit();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (manager.getTransaction().isActive())
				manager.getTransaction().rollback();

		} finally {
			manager.close();
		}

	}

	@Override
	public boolean atualizar(Tradicional t) {
		manager = fac.createEntityManager();
		boolean ret = false;
		try {
			dao.setManager(manager);
			ItemPedidoDAO ipdao = new ItemPedidoDAO();
			ipdao.setManager(manager);

			// se entidade for nula
			if (t == null) {
				throw new Exception("Entidade passada para atualiza��o � nula");
			}

			dao.atualizar(t);
			manager.getTransaction().begin();
			manager.getTransaction().commit();
			ret = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (manager.getTransaction().isActive())
				manager.getTransaction().rollback();
		} finally {
			manager.close();
		}
		return ret;
	}

	@Override
	public boolean remover(Tradicional t) {
		manager = fac.createEntityManager();
		boolean ret = false;
		try {
			dao.setManager(manager);
			ItemPedidoDAO ipdao = new ItemPedidoDAO();
			ipdao.setManager(manager);

			// se entidade for nula
			if (t == null) {
				throw new Exception("Entidade passada para remo��o � nula");

			} else
				t = dao.buscarPorId(t.getId());

			// se entidade n�o estiver BD
			if (t == null)
				throw new Exception("Entidade passada para remo��o n�o encontrada");

			// removendo cada item antes de pedido
			for (ItemPedido i : t.getItens()) {
				ipdao.remover(i);
			}

			dao.remover(t);
			manager.getTransaction().begin();
			manager.getTransaction().commit();
			ret = true;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (manager.getTransaction().isActive())
				manager.getTransaction().rollback();
		} finally {
			manager.close();
		}
		return ret;
	}

}
