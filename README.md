# trabalho1_cg
Universidade Federal de Pelotas Bacharelado em Ciência da Computação

Bruno G. Pinto - 11107588 - bgpinto@inf.ufpel.edu.br 	
Pablo Margreff - 14100513 - pmargreff@inf.ufpel.edu.br

1 # Considerações Gerais
Devido ao tratamento do sistema de coordenatdas de janela do java crescer para baixo em y, toda vez que o valor da raio somado ao valor de y_0(central) é muito grande ocorre um deslocamento do semi-circulo para fora da janela. 

2 # Casos de teste
Sintaxe: 
java Main < raio > < segmentos > < repeticoes > < x_0 >  < y_0 >

java Main 100 13 2 110 20
java Main 15 5 5 30 10
java Main 200 19 2 210 10
java Main 150 2 3 160 20
java Main 500 250 1 520 20

Formato do arquivo data/point_list
0 à 9 - "Ponto âncora"
   10 - testa
   11 - olho osquerdo
   12 - olho direito
   13 - lábio superior
   14 - queixo
   15 - ombro esquerdo 
   16 - ombro direito