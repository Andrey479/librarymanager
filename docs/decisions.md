## ADR-001: Injeção de `secretKey` via construtor no `JwtService`

**Contexto:** `JwtService` usava `@Setter` (Lombok) em nível de classe para expor
`setSecretKey(String)`, com o único propósito de permitir que
`JwtServiceTest` definisse a chave manualmente fora do contexto Spring
(`new JwtService(); jwtService.setSecretKey("...")`), já que `@Value` só é
resolvido quando o bean é gerenciado pelo container.

**Problema:** o setter é `public`. Qualquer código com referência ao bean
`JwtService` — em qualquer pacote — podia sobrescrever a chave de assinatura
JWT em runtime. Viola o Princípio do Menor Privilégio: o campo só deveria
ser definido uma vez, na construção, e nunca mais mutado.

**Decisão:** `secretKey` tornado `final`, injetado via construtor explícito
com `@Value(...)` no **parâmetro** do construtor (não no campo). `@Setter`
de classe e `@RequiredArgsConstructor` removidos.

**Alternativa considerada:** `ReflectionTestUtils.setField(...)`.

**Motivo da rejeição:** contorna tipagem/visibilidade via reflection,
esconde a dependência real da classe e acopla o teste a uma API do Spring
para simular o que injeção via construtor resolve nativamente.

**Trade-off aceito:** nenhum relevante — correção estritamente melhor.

**Nota técnica:** primeira tentativa (`final` + `@RequiredArgsConstructor`)
quebrou o contexto Spring. Causa raiz: Lombok copia tipo/nome do campo para
o construtor gerado, mas não copia anotações do campo — `@Value` ficou
"preso" ao campo-fonte. Lição: anotações de injeção devem ficar no
parâmetro quando o construtor é manual.

**Resposta de entrevista:** "Setter público numa chave JWT era superfície
de mutação desnecessária, só para viabilizar teste. Troquei por injeção via
construtor: compilador garante imutabilidade, teste se adapta ao design
correto — não o contrário."