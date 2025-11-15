import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { InsumoService } from '../../../services/insumo.service';
import { MovimentacaoInsumoService } from '../../../services/movimentacao-insumo.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NavbarComponent, CardModule, ChartModule],
  templateUrl: './dashboard.component.html'
  ,
  styles: [`
    :host ::ng-deep {
      /* Small stat cards (Total / Críticos) */
      .stat-small {
        padding: 0.5rem;
      }

      .stat-small .text-3xl {
        font-size: 1.25rem; /* smaller number */
      }

      .stat-small .text-900.font-bold {
        font-size: 1.6rem;
      }

      .stat-small .stat-icon {
        width: 2.2rem !important;
        height: 2.2rem !important;
      }

      .stat-small .stat-icon i {
        font-size: 1rem !important;
      }

      /* Chart cards: make charts smaller and side-by-side */
      .chart-small p-chart, .chart-small canvas {
        max-height: 160px !important;
        height: 160px !important;
      }

      .chart-small .p-card-content {
        padding: 0.45rem 0.6rem;
      }

      /* Page and heading sizes */
      h1 {
        font-size: 1.25rem; /* smaller page title */
        margin-bottom: 0.5rem;
      }

      .p-card h3, .p-card .m-0 {
        font-size: 1rem; /* smaller card titles */
      }

      /* Make stat labels smaller */
      .p-card .text-500 {
        font-size: 0.75rem;
      }

      /* Smaller overall text inside dashboard cards */
      .p-card {
        font-size: 0.9rem;
      }
    }
  `]
})
export class DashboardComponent implements OnInit {
  private insumoService = inject(InsumoService);
  private movimentacaoService = inject(MovimentacaoInsumoService);

  totalInsumos = 0;
  insumosCriticos = 0;
  movimentacoesMes = 0;
  entradasMes = 0;

  pieChartData: any;
  pieChartOptions: any;
  barChartData: any;
  barChartOptions: any;

  ngOnInit() {
    this.loadDashboardData();
    this.setupCharts();
  }

  loadDashboardData() {
    this.insumoService.findAll().subscribe(insumos => {
      this.totalInsumos = insumos.length;
      this.insumosCriticos = insumos.filter(i => 
        (i.quantidade_critica || 0) < 10
      ).length;
    });

    this.movimentacaoService.findAll().subscribe(movimentacoes => {
      const now = new Date();
      const currentMonth = now.getMonth();
      const currentYear = now.getFullYear();

      const movimentacoesMesAtual = movimentacoes.filter(m => {
        const data = new Date(m.data || '');
        return data.getMonth() === currentMonth && data.getFullYear() === currentYear;
      });

      this.movimentacoesMes = movimentacoesMesAtual.length;
      this.entradasMes = movimentacoesMesAtual.filter(m => 
        m.tipo_movimentacao === 'ENTRADA'
      ).length;

      this.updatePieChart(movimentacoes);
      this.updateBarChart(movimentacoes);
    });
  }

  setupCharts() {
    this.pieChartOptions = {
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            font: {
              size: 12
            }
          }
        }
      }
    };

    this.barChartOptions = {
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          ticks: {
            font: { size: 11 }
          }
        },
        y: {
          beginAtZero: true,
          ticks: {
            font: { size: 11 }
          }
        }
      }
    };
  }

  updatePieChart(movimentacoes: any[]) {
    const entradas = movimentacoes.filter(m => m.tipo_movimentacao === 'ENTRADA').length;
    const saidas = movimentacoes.filter(m => m.tipo_movimentacao === 'SAIDA').length;

    this.pieChartData = {
      labels: ['Entradas', 'Saídas'],
      datasets: [{
        data: [entradas, saidas],
        backgroundColor: ['#4CAF50', '#F44336']
      }]
    };
  }

  updateBarChart(movimentacoes: any[]) {
    const last7Days = Array.from({length: 7}, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (6 - i));
      return date;
    });

    const labels = last7Days.map(d => 
      d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })
    );

    const data = last7Days.map(day => {
      return movimentacoes.filter(m => {
        const mDate = new Date(m.data || '');
        return mDate.toDateString() === day.toDateString();
      }).length;
    });

    this.barChartData = {
      labels: labels,
      datasets: [{
        label: 'Movimentações',
        data: data,
        backgroundColor: '#2196F3'
      }]
    };
  }
}
