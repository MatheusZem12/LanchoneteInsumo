export interface Insumo {
  id?: number;
  codigo: string;
  nome: string;
  descricao?: string;
  quantidade_critica: number;
  quantidade_estoque?: number;
}
