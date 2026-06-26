<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="820px"
    align-center
    destroy-on-close
    class="employee-dialog"
    @keydown.esc="handleClose"
    @keydown.ctrl.enter="handleSubmit"
  >
    <div class="dialog-header">
      <el-upload
        class="avatar-uploader"
        action="#"
        :auto-upload="false"
        :show-file-list="false"
        :on-change="handleAvatarChange"
        accept="image/*"
      >
        <div class="dialog-avatar" :style="{ backgroundColor: form.avatar ? 'transparent' : getAvatarColor(form.name) }">
          <img v-if="form.avatar" :src="form.avatar" class="avatar-img" />
          <span v-else>{{ form.name?.charAt(0) || '?' }}</span>
          <div class="avatar-upload-icon">
            <el-icon><Camera /></el-icon>
          </div>
        </div>
      </el-upload>
      <div class="dialog-header-text">
        <div class="dialog-title">{{ dialogTitle }}</div>
        <div class="dialog-subtitle">请填写员工基本信息与完整档案，带 <span class="required-star">*</span> 为必填项</div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="form-tabs">
      <!-- Tab 1: 个人信息 -->
      <el-tab-pane label="个人信息" name="personal">
        <div class="dialog-section">
          <div class="section-title"><span class="section-dot blue" />基础资料</div>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="90px" class="dialog-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="工号">
                  <el-input :model-value="isEdit ? form.employeeNo : '保存后自动生成'" disabled>
                    <template #prefix><el-icon><CollectionTag /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="姓名" prop="name" required>
                  <el-input v-model="form.name" placeholder="请输入姓名" clearable>
                    <template #prefix><el-icon><User /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="年龄" prop="age">
                  <el-input-number v-model="form.age" :min="18" :max="65" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="form.gender" class="full-width-radio">
                    <el-radio :label="0">女</el-radio>
                    <el-radio :label="1">男</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="form.status" class="full-width-radio">
                    <el-radio :label="0">离职</el-radio>
                    <el-radio :label="1">在职</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="民族">
                  <el-input v-model="form.nation" placeholder="如：汉" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="籍贯">
                  <el-input v-model="form.nativePlace" placeholder="如：河南郑州" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="婚姻">
                  <el-select v-model="form.maritalStatus" placeholder="请选择" clearable class="full-width">
                    <el-option :value="0" label="未婚" />
                    <el-option :value="1" label="已婚" />
                    <el-option :value="2" label="离异" />
                    <el-option :value="3" label="丧偶" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="政治面貌">
                  <el-select v-model="form.politicalStatus" placeholder="请选择" clearable class="full-width">
                    <el-option value="群众" label="群众" />
                    <el-option value="团员" label="团员" />
                    <el-option value="党员" label="党员" />
                    <el-option value="民主党派" label="民主党派" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入职日期" prop="hireDate" required>
                  <el-date-picker v-model="form.hireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" class="full-width" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <div class="dialog-section">
          <div class="section-title"><span class="section-dot green" />联系方式</div>
          <el-form :model="form" :rules="rules" ref="formRef2" label-width="90px" class="dialog-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone" required>
                  <el-input v-model="form.phone" placeholder="请输入手机号" clearable>
                    <template #prefix><el-icon><Phone /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱" clearable>
                    <template #prefix><el-icon><Message /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input v-model="form.idCard" placeholder="请输入身份证号" clearable>
                    <template #prefix><el-icon><Postcard /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="银行卡号" prop="bankCard">
                  <el-input v-model="form.bankCard" placeholder="请输入银行卡号" clearable>
                    <template #prefix><el-icon><CreditCard /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="紧急联系人">
                  <el-input v-model="form.emergencyContact" placeholder="如：配偶/父母" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="紧急电话">
                  <el-input v-model="form.emergencyPhone" placeholder="请输入联系电话" clearable />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="现住址">
              <el-input v-model="form.currentAddress" placeholder="详细到街道/门牌" clearable />
            </el-form-item>
            <el-form-item label="户籍地址">
              <el-input v-model="form.hukouAddress" placeholder="身份证上的户籍地址" clearable />
            </el-form-item>
          </el-form>
        </div>

        <div class="dialog-section">
          <div class="section-title">
            <span class="section-dot success" />家庭成员
            <el-button size="small" type="primary" plain :icon="Plus" class="add-btn" @click="addFam">添加</el-button>
          </div>
          <el-empty v-if="!families.length" description="暂无登记家庭成员" :image-size="40" />
          <div v-for="(f, idx) in families" :key="idx" class="sub-item">
            <el-form :model="f" label-width="90px" class="sub-form">
              <el-row :gutter="14">
                <el-col :span="8">
                  <el-form-item label="姓名" required>
                    <el-input v-model="f.name" placeholder="姓名" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="关系" required>
                    <el-select v-model="f.relation" placeholder="关系">
                      <el-option value="配偶" label="配偶" />
                      <el-option value="父亲" label="父亲" />
                      <el-option value="母亲" label="母亲" />
                      <el-option value="儿子" label="儿子" />
                      <el-option value="女儿" label="女儿" />
                      <el-option value="兄弟" label="兄弟" />
                      <el-option value="姐妹" label="姐妹" />
                      <el-option value="其他" label="其他" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item label="性别">
                    <el-radio-group v-model="f.gender">
                      <el-radio :label="0">女</el-radio>
                      <el-radio :label="1">男</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="出生日期">
                    <el-date-picker v-model="f.birthDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="出生日期" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="12">
                  <el-form-item label="工作单位">
                    <el-input v-model="f.company" placeholder="选填" />
                  </el-form-item>
                </el-col>
                <el-col :span="10">
                  <el-form-item label="联系电话">
                    <el-input v-model="f.phone" placeholder="选填" />
                  </el-form-item>
                </el-col>
                <el-col :span="2">
                  <el-form-item label=" ">
                    <el-button type="danger" link :icon="Delete" @click="families.splice(idx, 1)" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab 2: 个人背景 -->
      <el-tab-pane label="个人背景" name="career">
        <div class="dialog-section">
          <div class="section-title">
            <span class="section-dot purple" />教育经历
            <el-button size="small" type="primary" plain :icon="Plus" class="add-btn" @click="addEdu">添加</el-button>
          </div>
          <el-empty v-if="!educations.length" description="暂无教育经历" :image-size="40" />
          <div v-for="(e, idx) in educations" :key="idx" class="sub-item">
            <el-form :model="e" label-width="90px" class="sub-form">
              <el-row :gutter="14">
                <el-col :span="10">
                  <el-form-item label="学校" required>
                    <el-input v-model="e.school" placeholder="学校名称" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="专业">
                    <el-input v-model="e.major" placeholder="专业" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="学历">
                    <el-select v-model="e.education" placeholder="学历" clearable>
                      <el-option value="高中" label="高中" />
                      <el-option value="大专" label="大专" />
                      <el-option value="本科" label="本科" />
                      <el-option value="硕士" label="硕士" />
                      <el-option value="博士" label="博士" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="6">
                  <el-form-item label="学位">
                    <el-select v-model="e.degree" placeholder="学位" clearable>
                      <el-option value="无" label="无" />
                      <el-option value="学士" label="学士" />
                      <el-option value="硕士" label="硕士" />
                      <el-option value="博士" label="博士" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="入学">
                    <el-date-picker v-model="e.startDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="入学日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="毕业">
                    <el-date-picker v-model="e.endDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="毕业日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="2">
                  <el-form-item label=" ">
                    <el-button type="danger" link :icon="Delete" @click="educations.splice(idx, 1)" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="全日制">
                <el-switch v-model="e.isFullTime" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div class="dialog-section">
          <div class="section-title">
            <span class="section-dot warning" />工作经历
            <el-button size="small" type="primary" plain :icon="Plus" class="add-btn" @click="addExp">添加</el-button>
          </div>
          <el-empty v-if="!workExperiences.length" description="暂无工作经历" :image-size="40" />
          <div v-for="(w, idx) in workExperiences" :key="idx" class="sub-item">
            <el-form :model="w" label-width="90px" class="sub-form">
              <el-row :gutter="14">
                <el-col :span="10">
                  <el-form-item label="公司" required>
                    <el-input v-model="w.company" placeholder="公司名称" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="职位">
                    <el-input v-model="w.position" placeholder="岗位" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="部门">
                    <el-input v-model="w.department" placeholder="部门" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="8">
                  <el-form-item label="入职">
                    <el-date-picker v-model="w.startDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="入职日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="离职">
                    <el-date-picker v-model="w.endDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="离职日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="离职原因">
                    <el-input v-model="w.leaveReason" placeholder="选填" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label=" ">
                <el-button type="danger" link :icon="Delete" @click="workExperiences.splice(idx, 1)">删除本条</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div class="dialog-section">
          <div class="section-title">
            <span class="section-dot danger" />技能证书
            <el-button size="small" type="primary" plain :icon="Plus" class="add-btn" @click="addCert">添加</el-button>
          </div>
          <el-empty v-if="!certificates.length" description="暂无技能证书" :image-size="40" />
          <div v-for="(c, idx) in certificates" :key="idx" class="sub-item">
            <el-form :model="c" label-width="90px" class="sub-form">
              <el-row :gutter="14">
                <el-col :span="10">
                  <el-form-item label="证书名称" required>
                    <el-input v-model="c.name" placeholder="如：系统架构师" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="级别">
                    <el-select v-model="c.level" placeholder="级别" clearable>
                      <el-option value="初级" label="初级" />
                      <el-option value="中级" label="中级" />
                      <el-option value="高级" label="高级" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="发证机构">
                    <el-input v-model="c.issuer" placeholder="如：工信部" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="8">
                  <el-form-item label="发证日期">
                    <el-date-picker v-model="c.issueDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="发证日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="到期日期">
                    <el-date-picker v-model="c.expireDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="到期日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="证书编号">
                    <el-input v-model="c.certNo" placeholder="选填" />
                  </el-form-item>
                </el-col>
                <el-col :span="2">
                  <el-form-item label=" ">
                    <el-button type="danger" link :icon="Delete" @click="certificates.splice(idx, 1)" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab 3: 岗位合同 -->
      <el-tab-pane label="岗位合同" name="work_contract">
        <div class="dialog-section">
          <div class="section-title"><span class="section-dot orange" />工作信息</div>
          <el-form :model="form" :rules="rules" ref="formRef3" label-width="90px" class="dialog-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="部门" prop="department" required>
                  <el-select v-model="form.department" placeholder="请选择部门" clearable filterable class="full-width" @change="handleDepartmentChange">
                    <el-option v-for="dept in departmentOptions" :key="dept.code" :label="dept.name" :value="dept.code" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职位" prop="position" required>
                  <el-select v-model="form.position" placeholder="请选择职位" clearable filterable class="full-width" :disabled="!form.department">
                    <el-option v-for="pos in positionOptions" :key="pos.code" :label="pos.name" :value="pos.code" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="职级" prop="rank">
                  <el-select v-model="form.rank" placeholder="请选择职级" clearable filterable class="full-width">
                    <el-option v-for="r in rankOptions" :key="r.code" :label="r.name" :value="r.code" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="薪资" prop="salary" required>
                  <el-input-number v-model="form.salary" :min="0" :precision="2" :step="1000" class="full-width">
                    <template #suffix>元/月</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <div class="dialog-section">
          <div class="section-title">
            <span class="section-dot blue" />合同信息
            <el-button size="small" type="primary" plain :icon="Plus" class="add-btn" @click="addContract">添加</el-button>
          </div>
          <el-empty v-if="!contracts.length" description="暂无合同记录" :image-size="40" />
          <div v-for="(c, idx) in contracts" :key="idx" class="sub-item">
            <el-form :model="c" label-width="90px" class="sub-form">
              <el-row :gutter="14">
                <el-col :span="8">
                  <el-form-item label="合同类型" required>
                    <el-select v-model="c.contractType" placeholder="选择类型">
                      <el-option value="固定期限" label="固定期限" />
                      <el-option value="无固定期限" label="无固定期限" />
                      <el-option value="实习" label="实习" />
                      <el-option value="兼职" label="兼职" />
                      <el-option value="劳务派遣" label="劳务派遣" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="合同编号">
                    <el-input v-model="c.contractNo" placeholder="如：HT-2024-001" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="签订日期">
                    <el-date-picker v-model="c.signedDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="签订日期" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="8">
                  <el-form-item label="开始日期" required>
                    <el-date-picker v-model="c.startDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="开始日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="结束日期">
                    <el-date-picker v-model="c.endDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="无固定期为空" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="状态">
                    <el-select v-model="c.status" placeholder="状态">
                      <el-option :value="1" label="执行中" />
                      <el-option :value="0" label="已到期" />
                      <el-option :value="2" label="已终止" />
                      <el-option :value="3" label="已续签" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="6">
                  <el-form-item label="试用期月数">
                    <el-input-number v-model="c.probationMonths" :min="0" :max="12" class="full-width" />
                  </el-form-item>
                </el-col>
                <el-col :span="9">
                  <el-form-item label="试用期起">
                    <el-date-picker v-model="c.probationStartDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="试用期开始" />
                  </el-form-item>
                </el-col>
                <el-col :span="9">
                  <el-form-item label="试用期止">
                    <el-date-picker v-model="c.probationEndDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="试用期结束" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="12">
                  <el-form-item label="工作地点">
                    <el-input v-model="c.workLocation" placeholder="如：北京·总部" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="合同薪资">
                    <el-input-number v-model="c.salary" :min="0" :precision="2" :step="1000" class="full-width">
                      <template #suffix>元/月</template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="备注">
                <el-input v-model="c.remark" type="textarea" :rows="2" placeholder="选填" />
              </el-form-item>
              <el-form-item label=" ">
                <el-button type="danger" link :icon="Delete" @click="contracts.splice(idx, 1)">删除本条</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div class="dialog-section">
          <div class="section-title">
            <span class="section-dot orange" />试用期记录
            <el-button size="small" type="primary" plain :icon="Plus" class="add-btn" @click="addProbation">添加</el-button>
          </div>
          <el-empty v-if="!probations.length" description="暂无试用期记录" :image-size="40" />
          <div v-for="(p, idx) in probations" :key="idx" class="sub-item">
            <el-form :model="p" label-width="90px" class="sub-form">
              <el-row :gutter="14">
                <el-col :span="12">
                  <el-form-item label="开始日期" required>
                    <el-date-picker v-model="p.startDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="开始日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="结束日期" required>
                    <el-date-picker v-model="p.endDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="结束日期" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="14">
                <el-col :span="8">
                  <el-form-item label="考核结果">
                    <el-select v-model="p.result" placeholder="结果" clearable>
                      <el-option :value="0" label="未通过" />
                      <el-option :value="1" label="提前转正" />
                      <el-option :value="2" label="正常转正" />
                      <el-option :value="3" label="延长试用期" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="结果日期">
                    <el-date-picker v-model="p.resultDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="结果日期" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="考核人">
                    <el-input v-model="p.evaluator" placeholder="如：张经理" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="延长至" v-if="p.result === 3">
                <el-date-picker v-model="p.extensionEndDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="延长后结束日期" />
              </el-form-item>
              <el-form-item label="考核评语">
                <el-input v-model="p.evaluation" type="textarea" :rows="2" placeholder="选填" />
              </el-form-item>
              <el-form-item label=" ">
                <el-button type="danger" link :icon="Delete" @click="probations.splice(idx, 1)">删除本条</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :icon="Check" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Check,
  User,
  Phone,
  Message,
  CollectionTag,
  Postcard,
  CreditCard,
  Camera,
  Plus,
  Delete,
} from '@element-plus/icons-vue'
import { type Employee, type EmployeeFormPayload, type EmployeeEducation, type EmployeeWorkExperience, type EmployeeFamily, type EmployeeCertificate, type EmployeeContract, type EmployeeProbation } from '@/api/employee'
import { type Dictionary } from '@/api/dictionary'
import { fileApi } from '@/api/file'
import { getAvatarColor } from '@/utils/common'

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  modelValue?: Employee
  detail?: {
    educations?: EmployeeEducation[]
    workExperiences?: EmployeeWorkExperience[]
    families?: EmployeeFamily[]
    certificates?: EmployeeCertificate[]
    contracts?: EmployeeContract[]
    probations?: EmployeeProbation[]
  } | null
  departmentOptions: Dictionary[]
  positionOptionsAll: Dictionary[]
  rankOptions: Dictionary[]
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'submit', data: EmployeeFormPayload): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const activeTab = ref('personal')
const formRef = ref<any>(null)
const formRef2 = ref<any>(null)
const formRef3 = ref<any>(null)

const form = reactive<Employee>({
  name: '',
  age: 22,
  gender: 1,
  phone: '',
  email: '',
  department: '',
  position: '',
  rank: '',
  salary: 0,
  hireDate: '',
  avatar: '',
  status: 1,
  maritalStatus: 0,
  nation: '',
  nativePlace: '',
  politicalStatus: '',
  emergencyContact: '',
  emergencyPhone: '',
  currentAddress: '',
  hukouAddress: '',
})

const educations = reactive<EmployeeEducation[]>([])
const workExperiences = reactive<EmployeeWorkExperience[]>([])
const families = reactive<EmployeeFamily[]>([])
const certificates = reactive<EmployeeCertificate[]>([])
const contracts = reactive<EmployeeContract[]>([])
const probations = reactive<EmployeeProbation[]>([])

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
}

const dialogTitle = computed(() => props.isEdit ? '编辑员工' : '新增员工')

const positionOptions = computed(() => {
  if (!form.department) return []
  return props.positionOptionsAll.filter((item) => item.parentCode === form.department)
})

const resetForm = () => {
  form.name = ''
  form.age = 22
  form.gender = 1
  form.phone = ''
  form.email = ''
  form.department = ''
  form.position = ''
  form.rank = ''
  form.salary = 0
  form.hireDate = ''
  form.avatar = ''
  form.status = 1
  form.maritalStatus = 0
  form.nation = ''
  form.nativePlace = ''
  form.politicalStatus = ''
  form.emergencyContact = ''
  form.emergencyPhone = ''
  form.currentAddress = ''
  form.hukouAddress = ''
  form.idCard = ''
  form.bankCard = ''
  educations.splice(0, educations.length)
  workExperiences.splice(0, workExperiences.length)
  families.splice(0, families.length)
  certificates.splice(0, certificates.length)
  contracts.splice(0, contracts.length)
  probations.splice(0, probations.length)
}

const handleAvatarChange = async (uploadFile: any) => {
  const rawFile = uploadFile.raw
  if (!rawFile) return
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  if (rawFile.size > 2 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 2MB')
    return
  }
  try {
    const url = await fileApi.upload(rawFile)
    form.avatar = url
    ElMessage.success('头像上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '上传失败')
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    activeTab.value = 'personal'
    if (props.isEdit && props.modelValue) {
      Object.assign(form, props.modelValue)
    } else {
      resetForm()
    }
    if (props.detail) {
      educations.splice(0, educations.length, ...(props.detail.educations || []))
      workExperiences.splice(0, workExperiences.length, ...(props.detail.workExperiences || []))
      families.splice(0, families.length, ...(props.detail.families || []))
      certificates.splice(0, certificates.length, ...(props.detail.certificates || []))
      contracts.splice(0, contracts.length, ...(props.detail.contracts || []))
      probations.splice(0, probations.length, ...(props.detail.probations || []))
    } else {
      educations.splice(0, educations.length)
      workExperiences.splice(0, workExperiences.length)
      families.splice(0, families.length)
      certificates.splice(0, certificates.length)
      contracts.splice(0, contracts.length)
      probations.splice(0, probations.length)
    }
    nextTick(() => {
      formRef.value?.clearValidate()
      formRef2.value?.clearValidate()
      formRef3.value?.clearValidate()
    })
  }
})

const handleDepartmentChange = () => {
  form.position = ''
}

const handleClose = () => {
  emit('update:visible', false)
}

const addEdu = () => educations.push({ school: '', isFullTime: 1 })
const addExp = () => workExperiences.push({ company: '' })
const addFam = () => families.push({ name: '', relation: '其他', gender: 0 })
const addCert = () => certificates.push({ name: '' })
const addContract = () => contracts.push({ contractType: '固定期限', status: 1, probationMonths: 0, startDate: '' })
const addProbation = () => probations.push({ startDate: '', endDate: '' })

const handleSubmit = async () => {
  const valid1 = await formRef.value?.validate().catch(() => false)
  const valid2 = await formRef2.value?.validate().catch(() => false)
  const valid3 = await formRef3.value?.validate().catch(() => false)
  if (!valid1 || !valid2 || !valid3) return

  // 过滤空行
  const edus = educations.filter((e) => e.school)
  const exps = workExperiences.filter((w) => w.company)
  const fams = families.filter((f) => f.name && f.relation)
  const certs = certificates.filter((c) => c.name)
  const cons = contracts.filter((c) => c.contractType && c.startDate)
  const probs = probations.filter((p) => p.startDate && p.endDate)

  emit('submit', {
    employee: { ...form },
    educations: edus,
    workExperiences: exps,
    families: fams,
    certificates: certs,
    contracts: cons,
    probations: probs,
  })
}
</script>

<style scoped>
.employee-dialog :deep(.el-dialog__header) {
  padding: 0;
  border-bottom: none;
}

.employee-dialog :deep(.el-dialog__body) {
  padding: 0 20px 12px;
  max-height: 70vh;
  overflow-y: auto;
}

/* Tab 导航样式 */
.form-tabs {
  margin: 0 -4px;
}

.form-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}

.form-tabs :deep(.el-tabs__nav-wrap)::after {
  background: transparent;
}

.form-tabs :deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-tertiary);
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
  transition: all var(--dur-base) var(--ease-out);
}

.form-tabs :deep(.el-tabs__item:hover) {
  color: var(--brand-500);
}

.form-tabs :deep(.el-tabs__item.is-active) {
  color: var(--brand-600);
  background: linear-gradient(180deg, transparent 0%, rgba(99, 102, 241, 0.08) 100%);
}

.form-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  background: linear-gradient(90deg, var(--brand-400), var(--brand-600));
  border-radius: 2px 2px 0 0;
}

/* 弹窗头部区域 - 员工表单特殊样式 */
.dialog-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  margin: 0 -20px 16px;
  background: linear-gradient(135deg, var(--brand-50) 0%, var(--ink-100) 100%);
  border-bottom: 1px solid rgba(99, 102, 241, 0.12);
  position: relative;
}

html.dark .dialog-header {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.08) 0%, var(--ink-900) 100%);
  border-bottom-color: rgba(99, 102, 241, 0.15);
}

.dialog-avatar {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--ink-0);
  font-size: 22px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--dur-base) var(--ease-out), box-shadow var(--dur-base) var(--ease-out);
}

.dialog-avatar:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-upload-icon {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity var(--dur-fast);
  font-size: 18px;
  pointer-events: none;
  backdrop-filter: blur(2px);
}

.dialog-avatar:hover .avatar-upload-icon {
  opacity: 1;
}

.avatar-uploader :deep(.el-upload) {
  display: block;
}

.dialog-header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.dialog-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.dialog-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.required-star {
  color: var(--rose-500);
  font-weight: 600;
}

/* 表单分区样式 */
.dialog-section {
  margin-bottom: 16px;
  padding: 16px 18px;
  border-radius: var(--radius-lg);
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  transition: border-color var(--dur-fast);
}

html.dark .dialog-section {
  background: rgba(255, 255, 255, 0.03);
  border-color: rgba(255, 255, 255, 0.06);
}

.dialog-section:hover {
  border-color: var(--border-default);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 14px;
  letter-spacing: -0.01em;
}

.section-dot {
  width: 8px;
  height: 8px;
  border-radius: var(--radius-full);
  flex-shrink: 0;
}

.section-dot.blue { 
  background: var(--brand-500); 
  box-shadow: 0 0 6px rgba(99, 102, 241, 0.4); 
}
.section-dot.green { 
  background: var(--emerald-500); 
  box-shadow: 0 0 6px rgba(16, 185, 129, 0.4); 
}
.section-dot.orange { 
  background: var(--amber-500); 
  box-shadow: 0 0 6px rgba(245, 158, 11, 0.4); 
}
.section-dot.purple { 
  background: var(--brand-400); 
  box-shadow: 0 0 6px rgba(129, 140, 248, 0.4); 
}
.section-dot.warning { 
  background: var(--amber-500); 
  box-shadow: 0 0 6px rgba(245, 158, 11, 0.4); 
}
.section-dot.success { 
  background: var(--emerald-500); 
  box-shadow: 0 0 6px rgba(16, 185, 129, 0.4); 
}
.section-dot.danger { 
  background: var(--rose-500); 
  box-shadow: 0 0 6px rgba(244, 63, 94, 0.4); 
}

.add-btn {
  margin-left: auto;
}

.dialog-form .el-input,
.dialog-form .el-input-number,
.dialog-form .el-date-picker {
  width: 100%;
}

.dialog-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.dialog-form :deep(.el-form-item__label) {
  font-size: 13px;
}

.full-width {
  width: 100%;
}

.full-width-radio {
  width: 100%;
  display: flex;
}

.sub-item {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
}

.sub-item:last-child {
  margin-bottom: 0;
}

.sub-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.sub-form :deep(.el-form-item__label) {
  font-size: 12px;
}
</style>
