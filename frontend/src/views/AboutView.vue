<template>
  <div class="about-container">
    <div class="content-wrapper">
      <header class="header-section">
        <h1>ChatNCHU 興大校務通</h1>
        <p class="subtitle">結合 RAG 技術的校園規章智慧問答平台</p>
      </header>

      <section class="info-grid">
        <div class="card goal-card">
          <h3>🎯 平台目標</h3>
          <ul>
            <li>整合分散的公告與 PDF，轉化為可檢索結構化資料。</li>
            <li>結合向量資料庫與 LLM，提供具來源脈絡的回答。</li>
            <li>降低行政溝通成本，解決規章版本不明問題。</li>
          </ul>
        </div>

        <div class="card tech-card">
          <h3>🛠️ 核心技術</h3>
          <div class="tech-tags">
            <span class="tag">Vue 3</span>
            <span class="tag">Spring Boot</span>
            <span class="tag">Llama 3</span>
            <span class="tag">RAG</span>
            <span class="tag">Qdrant</span>
          </div>
        </div>
      </section>

      <section class="team-section">
        <h3>👥 開發團隊</h3>
        <div class="team-grid">
          <div class="member-card">
            <div class="role">PM / Backend / DevOps</div>
            <div class="name">陳鉦元</div>
            <div class="desc">進度控管、後端架構與系統維運</div>
          </div>
          <div class="member-card">
            <div class="role">UI / Frontend</div>
            <div class="name">洪慧珊</div>
            <div class="desc">介面設計、Vue 3 開發與體驗優化</div>
          </div>
          <div class="member-card">
            <div class="role">Data Engineer</div>
            <div class="name">許巧琳</div>
            <div class="desc">資料爬取、清洗與結構化處理</div>
          </div>
          <div class="member-card">
            <div class="role">LLM Engineer</div>
            <div class="name">張允</div>
            <div class="desc">Prompt 建構、RAG 檢索優化</div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.about-container {
  height: 100%;
  overflow-y: auto;
  background-color: #f8fafc;
  padding: 3rem 2rem;
}

.content-wrapper {
  max-width: 900px;
  margin: 0 auto;
}

.header-section {
  text-align: center;
  margin-bottom: 3rem;
}

h1 {
  font-size: 2.5rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.subtitle {
  font-size: 1.1rem;
  color: #64748b;
}

h3 {
  color: #334155;
  margin-bottom: 1rem;
  border-left: 4px solid #2563eb;
  padding-left: 10px;
}

/* 格線系統 */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.goal-card ul {
  padding-left: 1.2rem;
  color: #475569;
  line-height: 1.6;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  background: #e0f2fe;
  color: #0369a1;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

/* 團隊區塊 */
.team-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.member-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
  text-align: center;
}

.member-card:hover {
  transform: translateY(-5px);
}

.role {
  font-size: 0.8rem;
  color: #2563eb;
  font-weight: bold;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.name {
  font-size: 1.2rem;
  font-weight: bold;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.desc {
  font-size: 0.9rem;
  color: #64748b;
  line-height: 1.4;
}
</style>
