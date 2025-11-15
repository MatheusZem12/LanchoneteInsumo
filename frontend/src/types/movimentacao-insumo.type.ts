import { TipoMovimentacao } from './tipo-movimentacao.enum';

export interface MovimentacaoInsumo {
  id?: number;
  quantidade: number;
  tipo_movimentacao: TipoMovimentacao | string;
  data?: string;
  usuario_id: number;
  insumo_id: number;
  insumo_nome?: string;
  insumo_codigo?: string;
}
