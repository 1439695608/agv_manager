<template>
  <a-card style="width: 100%" class="public-doc" :loading="loading">
    <a-card-meta
      :title="article.title"
      :description="article.date.curr + ' · ' + article.author + ' · 字数：' + article.wc"/>
    <span v-html="article.content" class="article-content">
    </span>
  </a-card>
</template>
<script>
export default {
  data () {
    return {
      loading: false,
      article: {
        title: '文档说明',
        content: '这里为文档内容',
        date: {
          curr: '2021-03-14 15:43:05'
        },
        author: '开发工程师',
        wc: 1000
      }
    }
  }
}
</script>
<style lang="less">
  .public-doc {
    .article-button {
      font-size: 1.2rem !important;
    }
    .ant-card-body {
      padding: 18px !important;
    }
    .ant-card-head {
      padding: 0 1rem;
    }
    .ant-card-meta {
      margin-bottom: 1rem;
    }
    .article-content {
      p {
        word-wrap: break-word;
        word-break: break-all;
        text-overflow: initial;
        white-space: normal;
        font-size: .9rem !important;
        margin-bottom: .8rem;
      }
    }
  }
</style>
