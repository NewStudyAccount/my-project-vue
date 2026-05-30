<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute, onBeforeRouteLeave } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useNotesStore } from '../stores/notes'
import { useCategoryStore } from '../stores/categories'

const router = useRouter()
const route = useRoute()
const notesStore = useNotesStore()
const categoryStore = useCategoryStore()

const isNew = computed(() => route.params.id === 'new' || route.path === '/note/new')
const noteId = computed(() => route.params.id as string)

const title = ref('')
const content = ref('')
const categoryId = ref<string | null>(null)
const isDirty = ref(false)

// 表单校验规则
const rules = {
  title: [{ required: true, message: '请输入笔记标题', trigger: 'blur' }],
}

// 加载已有笔记数据
onMounted(() => {
  if (!isNew.value && noteId.value) {
    const note = notesStore.getNoteById(noteId.value)
    if (note) {
      title.value = note.title
      content.value = note.content
      categoryId.value = note.categoryId
    } else {
      ElMessage.error('笔记不存在')
      router.push('/notes')
    }
  }
})

// 监听表单变化
function onFormChange() {
  isDirty.value = true
}

// 路由离开守卫
onBeforeRouteLeave(async (to, from, next) => {
  if (isDirty.value) {
    try {
      await ElMessageBox.confirm('未保存的更改将丢失，确定要离开吗？', '提示', {
        confirmButtonText: '确定离开',
        cancelButtonText: '取消',
        type: 'warning',
      })
      next()
    } catch {
      next(false)
    }
  } else {
    next()
  }
})

async function handleSave() {
  if (!title.value.trim()) {
    ElMessage.warning('请输入笔记标题')
    return
  }

  if (isNew.value) {
    notesStore.addNote({
      title: title.value.trim(),
      content: content.value,
      categoryId: categoryId.value,
    })
    ElMessage.success('笔记创建成功')
  } else {
    notesStore.updateNote(noteId.value, {
      title: title.value.trim(),
      content: content.value,
      categoryId: categoryId.value,
    })
    ElMessage.success('笔记保存成功')
  }

  isDirty.value = false
  router.push(isNew.value ? '/notes' : `/note/${noteId.value}`)
}
</script>

<template>
  <div class="note-edit-view">
    <div class="page-header">
      <h1>{{ isNew ? '新建笔记' : '编辑笔记' }}</h1>
    </div>

    <el-form :rules="rules" label-position="top">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="title"
          placeholder="请输入笔记标题"
          @input="onFormChange"
        />
      </el-form-item>

      <el-form-item label="分类">
        <el-select
          v-model="categoryId"
          placeholder="选择分类（可选）"
          clearable
          @change="onFormChange"
        >
          <el-option
            v-for="cat in categoryStore.categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="内容">
        <el-input
          v-model="content"
          type="textarea"
          :rows="12"
          placeholder="请输入笔记内容"
          @input="onFormChange"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSave">保存</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.note-edit-view {
  max-width: 800px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}
</style>
