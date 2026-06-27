<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="800px"
    align-center
    destroy-on-close
    class="employee-dialog"
    @keydown.esc="handleClose"
    @keydown.ctrl.enter="handleSubmit"
  >
    <!-- 弹窗头部：头像 + 标题 -->
    <div class="dialog-header">
      <div class="header-left">
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
            <span v-else class="avatar-placeholder">{{ form.name?.charAt(0) || '?' }}</span>
            <div class="avatar-upload-overlay">
              <el-icon><Camera /></el-icon>
              <span>上传</span>
            </div>
          </div>
        </el-upload>
        <div class="header-text">
          <div class="dialog-title-row">
            <div class="dialog-title">{{ dialogTitle }}</div>
            <span class="dialog-badge">员工档案</span>
          </div>
          <div class="dialog-subtitle">完善基础信息、背景资料与岗位信息</div>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="form-tabs">
      <!-- Tab 1: 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <div class="tab-content">
          <!-- 工作信息 -->
          <div class="form-section">
            <div class="section-label"><span class="label-dot work"></span>工作信息</div>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="72px" class="emp-form">
              <el-row :gutter="18">
                <el-col :span="8">
                  <el-form-item label="工号" class="info-only">
                    <el-input :model-value="isEdit ? form.employeeNo : '自动生成'" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="姓名" prop="name">
                    <el-input v-model="form.name" placeholder="请输入员工姓名" maxlength="20" clearable />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="入职" prop="hireDate">
                    <el-date-picker v-model="form.hireDate" type="date" placeholder="选择入职日期" value-format="YYYY-MM-DD" class="full-width" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="18">
                <el-col :span="8">
                  <el-form-item label="部门" prop="department">
                    <el-select v-model="form.department" placeholder="请选择部门" clearable filterable @change="handleDepartmentChange">
                      <el-option v-for="dept in departmentOptions" :key="dept.code" :label="dept.name" :value="dept.code" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="职位" prop="position">
                    <el-select v-model="form.position" placeholder="请先选择部门" clearable filterable :disabled="!form.department">
                      <el-option v-for="pos in positionOptions" :key="pos.code" :label="pos.name" :value="pos.code" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="职级">
                    <el-select v-model="form.rank" placeholder="请选择职级" clearable filterable class="full-width">
                      <el-option v-for="r in rankOptions" :key="r.code" :label="r.name" :value="r.code" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="18">
                <el-col :span="12">
                  <el-form-item label="月薪" prop="salary">
                    <el-input-number v-model="form.salary" :min="0" :max="999999" :precision="2" :step="500" class="full-width" placeholder="输入月薪" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="性别">
                    <el-radio-group v-model="form.gender">
                      <el-radio :label="1">男</el-radio>
                      <el-radio :label="0">女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>

          <!-- 基础资料 -->
          <div class="form-section">
            <div class="section-label"><span class="label-dot base"></span>基础资料</div>
            <el-form :model="form" :rules="rules" ref="formRef2" label-width="72px" class="emp-form">
              <el-row :gutter="18">
                <el-col :span="8">
                  <el-form-item label="年龄">
                    <el-input-number v-model="form.age" :min="18" :max="65" class="full-width" placeholder="18-65岁" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="民族">
                    <el-input v-model="form.nation" placeholder="如：汉族" clearable maxlength="20" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="籍贯">
                    <el-input v-model="form.nativePlace" placeholder="如：广东深圳" clearable maxlength="50" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="18">
                <el-col :span="12">
                  <el-form-item label="婚姻状态">
                    <el-select v-model="form.maritalStatus" placeholder="请选择" clearable class="full-width">
                      <el-option :value="0" label="未婚" />
                      <el-option :value="1" label="已婚" />
                      <el-option :value="2" label="离异" />
                      <el-option :value="3" label="丧偶" />
                    </el-select>
                  </el-form-item>
                </el-col>
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
              </el-row>
            </el-form>
          </div>

          <!-- 联系方式 -->
          <div class="form-section">
            <div class="section-label"><span class="label-dot contact"></span>联系方式</div>
            <el-form :model="form" :rules="rules" ref="formRef3" label-width="72px" class="emp-form">
              <el-row :gutter="18">
                <el-col :span="8">
                  <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone" placeholder="11位手机号码" clearable maxlength="11" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="邮箱">
                    <el-input v-model="form.email" placeholder="example@company.com" clearable />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="身份证">
                    <el-input v-model="form.idCard" placeholder="18位身份证号" clearable maxlength="18" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="18">
                <el-col :span="10">
                  <el-form-item label="银行卡">
                    <el-input v-model="form.bankCard" placeholder="工资卡号" clearable maxlength="23" />
                  </el-form-item>
                </el-col>
                <el-col :span="7">
                  <el-form-item label="紧急人">
                    <el-input v-model="form.emergencyContact" placeholder="姓名" clearable maxlength="20" />
                  </el-form-item>
                </el-col>
                <el-col :span="7">
                  <el-form-item label="紧急电话">
                    <el-input v-model="form.emergencyPhone" placeholder="联系电话" clearable maxlength="11" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="18">
                <el-col :span="12">
                  <el-form-item label="现住址">
                    <el-input v-model="form.currentAddress" placeholder="详细地址" clearable maxlength="100" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="户籍地">
                    <el-input v-model="form.hukouAddress" placeholder="户籍地址" clearable maxlength="100" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>

        </div>
      </el-tab-pane>

      <!-- Tab 2: 背景资料 -->
      <el-tab-pane label="背景资料" name="background">
        <div class="tab-content">
          <!-- 家庭成员 -->
          <div class="form-section">
            <div class="section-label-row">
              <div class="section-label"><span class="label-dot family"></span>家庭成员</div>
              <el-button size="small" type="primary" plain :icon="Plus" @click="addFam">添加成员</el-button>
            </div>
            <div v-if="!families.length" class="empty-tip">
              <el-icon><User /></el-icon>
              <span>暂无登记家庭成员</span>
            </div>
            <div v-else class="family-list">
              <div v-for="(f, idx) in families" :key="idx" class="family-card">
                <el-form :model="f" inline class="family-form">
                  <el-form-item label="姓名" required>
                    <el-input v-model="f.name" placeholder="姓名" style="width: 100px" />
                  </el-form-item>
                  <el-form-item label="关系" required>
                    <el-select v-model="f.relation" placeholder="关系" style="width: 90px">
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
                  <el-form-item label="电话">
                    <el-input v-model="f.phone" placeholder="电话" style="width: 120px" />
                  </el-form-item>
                  <el-form-item label="工作单位">
                    <el-input v-model="f.company" placeholder="单位" style="width: 140px" />
                  </el-form-item>
                  <el-form-item label=" ">
                    <el-button type="danger" link :icon="Delete" @click="families.splice(idx, 1)" />
                  </el-form-item>
                </el-form>
              </div>
            </div>
          </div>

          <!-- 教育经历 -->
          <div class="form-section">
            <div class="section-label-row">
              <div class="section-label"><span class="label-dot edu"></span>教育经历</div>
              <el-button size="small" type="primary" plain :icon="Plus" @click="addEdu">添加学历</el-button>
            </div>
            <div v-if="!educations.length" class="empty-tip">
              <el-icon><Reading /></el-icon>
              <span>暂无教育经历</span>
            </div>
            <div v-else class="sub-list">
              <div v-for="(e, idx) in educations" :key="idx" class="sub-card">
                <div class="sub-card-header">
                  <span class="sub-card-title">第 {{ idx + 1 }} 段学历</span>
                  <el-button type="danger" link :icon="Delete" @click="educations.splice(idx, 1)" />
                </div>
                <el-form :model="e" label-width="80px" class="sub-form">
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="学校" required>
                        <el-input v-model="e.school" placeholder="学校名称" />
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
                  </el-row>
                  <el-row :gutter="16">
                    <el-col :span="10">
                      <el-form-item label="专业">
                        <el-input v-model="e.major" placeholder="所学专业" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="7">
                      <el-form-item label="入学">
                        <el-date-picker v-model="e.startDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="入学日期" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="7">
                      <el-form-item label="毕业">
                        <el-date-picker v-model="e.endDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="毕业日期" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-form-item label="全日制">
                    <el-switch v-model="e.isFullTime" :active-value="1" :inactive-value="0" active-text="是" inactive-text="否" />
                  </el-form-item>
                </el-form>
              </div>
            </div>
          </div>

          <!-- 工作经历 -->
          <div class="form-section">
            <div class="section-label-row">
              <div class="section-label"><span class="label-dot work-exp"></span>工作经历</div>
              <el-button size="small" type="primary" plain :icon="Plus" @click="addExp">添加经历</el-button>
            </div>
            <div v-if="!workExperiences.length" class="empty-tip">
              <el-icon><Briefcase /></el-icon>
              <span>暂无工作经历</span>
            </div>
            <div v-else class="sub-list">
              <div v-for="(w, idx) in workExperiences" :key="idx" class="sub-card">
                <div class="sub-card-header">
                  <span class="sub-card-title">第 {{ idx + 1 }} 段经历</span>
                  <el-button type="danger" link :icon="Delete" @click="workExperiences.splice(idx, 1)" />
                </div>
                <el-form :model="w" label-width="80px" class="sub-form">
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="公司" required>
                        <el-input v-model="w.company" placeholder="公司名称" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="6">
                      <el-form-item label="职位">
                        <el-input v-model="w.position" placeholder="担任职位" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="6">
                      <el-form-item label="部门">
                        <el-input v-model="w.department" placeholder="所属部门" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="16">
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
                        <el-input v-model="w.leaveReason" placeholder="简要说明" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </div>
          </div>

          <!-- 技能证书 -->
          <div class="form-section">
            <div class="section-label-row">
              <div class="section-label"><span class="label-dot cert"></span>技能证书</div>
              <el-button size="small" type="primary" plain :icon="Plus" @click="addCert">添加证书</el-button>
            </div>
            <div v-if="!certificates.length" class="empty-tip">
              <el-icon><Medal /></el-icon>
              <span>暂无技能证书</span>
            </div>
            <div v-else class="sub-list">
              <div v-for="(c, idx) in certificates" :key="idx" class="sub-card">
                <div class="sub-card-header">
                  <span class="sub-card-title">第 {{ idx + 1 }} 本证书</span>
                  <el-button type="danger" link :icon="Delete" @click="certificates.splice(idx, 1)" />
                </div>
                <el-form :model="c" label-width="80px" class="sub-form">
                  <el-row :gutter="16">
                    <el-col :span="10">
                      <el-form-item label="证书名称" required>
                        <el-input v-model="c.name" placeholder="如：系统架构师" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="7">
                      <el-form-item label="级别">
                        <el-select v-model="c.level" placeholder="级别" clearable>
                          <el-option value="初级" label="初级" />
                          <el-option value="中级" label="中级" />
                          <el-option value="高级" label="高级" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="7">
                      <el-form-item label="发证机构">
                        <el-input v-model="c.issuer" placeholder="如：工信部" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="16">
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
                    <el-col :span="8">
                      <el-form-item label="证书编号">
                        <el-input v-model="c.certNo" placeholder="选填" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </div>
          </div>

          <!-- 合同信息 -->
          <div class="form-section">
            <div class="section-label-row">
              <div class="section-label"><span class="label-dot contract"></span>合同信息</div>
              <el-button size="small" type="primary" plain :icon="Plus" @click="addContract">添加合同</el-button>
            </div>
            <div v-if="!contracts.length" class="empty-tip">
              <el-icon><Document /></el-icon>
              <span>暂无合同记录</span>
            </div>
            <div v-else class="sub-list">
              <div v-for="(c, idx) in contracts" :key="idx" class="sub-card contract">
                <div class="sub-card-header">
                  <span class="sub-card-title">合同 {{ idx + 1 }}</span>
                  <el-tag size="small" type="info">{{ c.contractType || '未选择类型' }}</el-tag>
                  <el-button type="danger" link :icon="Delete" @click="contracts.splice(idx, 1)" />
                </div>
                <el-form :model="c" label-width="80px" class="sub-form">
                  <el-row :gutter="16">
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
                  <el-row :gutter="16">
                    <el-col :span="8">
                      <el-form-item label="开始日期" required>
                        <el-date-picker v-model="c.startDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="开始日期" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="结束日期">
                        <el-date-picker v-model="c.endDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="无固定期留空" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="工作地点">
                        <el-input v-model="c.workLocation" placeholder="如：北京·总部" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="16">
                    <el-col :span="8">
                      <el-form-item label="试用期">
                        <el-input-number v-model="c.probationMonths" :min="0" :max="12" class="full-width" placeholder="月数" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="合同薪资">
                        <el-input-number v-model="c.salary" :min="0" :precision="2" class="full-width" placeholder="元/月" />
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
                </el-form>
              </div>
            </div>
          </div>

          <!-- 试用期记录 -->
          <div class="form-section">
            <div class="section-label-row">
              <div class="section-label"><span class="label-dot probation"></span>试用期记录</div>
              <el-button size="small" type="primary" plain :icon="Plus" @click="addProbation">添加记录</el-button>
            </div>
            <div v-if="!probations.length" class="empty-tip">
              <el-icon><Timer /></el-icon>
              <span>暂无试用期记录</span>
            </div>
            <div v-else class="sub-list">
              <div v-for="(p, idx) in probations" :key="idx" class="sub-card">
                <div class="sub-card-header">
                  <span class="sub-card-title">第 {{ idx + 1 }} 次考核</span>
                  <el-button type="danger" link :icon="Delete" @click="probations.splice(idx, 1)" />
                </div>
                <el-form :model="p" label-width="80px" class="sub-form">
                  <el-row :gutter="16">
                    <el-col :span="8">
                      <el-form-item label="开始日期" required>
                        <el-date-picker v-model="p.startDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="开始日期" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="结束日期" required>
                        <el-date-picker v-model="p.endDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="结束日期" />
                      </el-form-item>
                    </el-col>
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
                  </el-row>
                  <el-row :gutter="16">
                    <el-col :span="8">
                      <el-form-item label="结果日期">
                        <el-date-picker v-model="p.resultDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="考核日期" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="考核人">
                        <el-input v-model="p.evaluator" placeholder="如：张经理" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8" v-if="p.result === 3">
                      <el-form-item label="延长至">
                        <el-date-picker v-model="p.extensionEndDate" type="date" value-format="YYYY-MM-DD" class="full-width" placeholder="延长后结束日期" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-form-item label="考核评语">
                    <el-input v-model="p.evaluation" type="textarea" :rows="2" placeholder="选填，简述考核情况" />
                  </el-form-item>
                </el-form>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :icon="Check" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '确认新增' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Check, User, Camera, Plus, Delete, InfoFilled, CircleCheck, Clock,
  Reading, Briefcase, Medal, Document, Timer,
} from '@element-plus/icons-vue'
import {
  type Employee, type EmployeeFormPayload,
  type EmployeeEducation, type EmployeeWorkExperience,
  type EmployeeFamily, type EmployeeCertificate,
  type EmployeeContract, type EmployeeProbation,
} from '@/api/employee'
import { type Dictionary } from '@/api/dictionary'
import { fileApi } from '@/api/file'
import { getAvatarColor } from '@/utils/common'

const MAX_AVATAR_SIZE = 5 * 1024 * 1024
const AVATAR_COMPRESS_THRESHOLD = 300 * 1024
const AVATAR_MAX_DIMENSION = 512
const AVATAR_JPEG_QUALITY = 0.82

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

const activeTab = ref('basic')
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
  idCard: '',
  bankCard: '',
})

const educations = reactive<EmployeeEducation[]>([])
const workExperiences = reactive<EmployeeWorkExperience[]>([])
const families = reactive<EmployeeFamily[]>([])
const certificates = reactive<EmployeeCertificate[]>([])
const contracts = reactive<EmployeeContract[]>([])
const probations = reactive<EmployeeProbation[]>([])

const rules = {
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度 2-20 个字符', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确（需 11 位）', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  department: [
    { required: true, message: '请选择所属部门', trigger: 'change' },
  ],
  position: [
    { required: true, message: '请选择职位', trigger: 'change' },
  ],
  hireDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' },
  ],
  salary: [
    { required: true, message: '请输入月薪', trigger: 'blur' },
  ],
}

const dialogTitle = computed(() => props.isEdit ? '编辑员工档案' : '新增员工')

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

const compressAvatarImage = (file: File): Promise<File> => {
  if (file.type === 'image/gif' || file.size <= AVATAR_COMPRESS_THRESHOLD) {
    return Promise.resolve(file)
  }

  return new Promise((resolve, reject) => {
    const imageUrl = URL.createObjectURL(file)
    const image = new Image()
    image.onload = () => {
      URL.revokeObjectURL(imageUrl)

      const scale = Math.min(1, AVATAR_MAX_DIMENSION / Math.max(image.width, image.height))
      const canvas = document.createElement('canvas')
      canvas.width = Math.max(1, Math.round(image.width * scale))
      canvas.height = Math.max(1, Math.round(image.height * scale))

      const ctx = canvas.getContext('2d')
      if (!ctx) {
        resolve(file)
        return
      }

      ctx.fillStyle = '#fff'
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      ctx.drawImage(image, 0, 0, canvas.width, canvas.height)

      canvas.toBlob((blob) => {
        if (!blob) {
          resolve(file)
          return
        }

        const compressedFile = new File(
          [blob],
          file.name.replace(/\.[^.]+$/, '') + '.jpg',
          { type: 'image/jpeg', lastModified: Date.now() }
        )
        resolve(compressedFile.size < file.size ? compressedFile : file)
      }, 'image/jpeg', AVATAR_JPEG_QUALITY)
    }
    image.onerror = () => {
      URL.revokeObjectURL(imageUrl)
      reject(new Error('图片读取失败'))
    }
    image.src = imageUrl
  })
}

const handleAvatarChange = async (uploadFile: any) => {
  const rawFile = uploadFile.raw
  if (!rawFile) return
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  if (rawFile.size > MAX_AVATAR_SIZE) {
    ElMessage.warning('头像图片大小不能超过 5MB')
    return
  }
  try {
    const uploadAvatar = await compressAvatarImage(rawFile)
    const url = await fileApi.upload(uploadAvatar)
    form.avatar = url
    const savedKb = Math.round((rawFile.size - uploadAvatar.size) / 1024)
    ElMessage.success(savedKb > 0 ? `头像已压缩并上传，减少约 ${savedKb}KB` : '头像上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    activeTab.value = 'basic'
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
  if (!valid1 || !valid2 || !valid3) {
    ElMessage.warning('请检查必填项是否填写完整')
    activeTab.value = 'basic'
    return
  }

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
/* ========== 弹窗基础 ========== */
.employee-dialog {
  height: 760px;
}

.employee-dialog :deep(.el-dialog__header) {
  padding: 0;
  margin-right: 0;
}

.employee-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.employee-dialog :deep(.el-dialog__footer) {
  padding: 12px 20px;
  border-top: 1px solid var(--border-subtle);
}

/* ========== 头部区域 ========== */
.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  background:
    linear-gradient(135deg, rgba(79, 70, 229, 0.08), rgba(14, 165, 233, 0.1)),
    linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  color: var(--text-primary);
  border-bottom: 1px solid rgba(99, 102, 241, 0.14);
  position: relative;
  overflow: hidden;
}

.dialog-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -8%;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.16), transparent 62%);
  pointer-events: none;
}

.dialog-header::after {
  content: '';
  position: absolute;
  bottom: -120%;
  left: 4%;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(20, 184, 166, 0.12), transparent 62%);
  pointer-events: none;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.avatar-uploader {
  flex-shrink: 0;
}

.dialog-avatar {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 19px;
  font-weight: 700;
  box-shadow:
    0 10px 24px rgba(79, 70, 229, 0.18),
    0 0 0 4px rgba(255, 255, 255, 0.86);
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dialog-avatar:hover {
  transform: translateY(-1px);
  box-shadow:
    0 14px 30px rgba(79, 70, 229, 0.24),
    0 0 0 4px rgba(255, 255, 255, 0.95);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.avatar-upload-overlay {
  position: absolute;
  inset: 0;
  background: rgba(17, 24, 39, 0.58);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.2s ease;
  font-size: 11px;
  font-weight: 600;
  backdrop-filter: blur(3px);
}

.dialog-avatar:hover .avatar-upload-overlay {
  opacity: 1;
}

.avatar-uploader :deep(.el-upload) {
  display: block;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.dialog-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dialog-title {
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.dialog-badge {
  height: 22px;
  display: inline-flex;
  align-items: center;
  padding: 0 8px;
  border-radius: 999px;
  background: rgba(79, 70, 229, 0.1);
  color: #4f46e5;
  font-size: 11px;
  font-weight: 700;
  border: 1px solid rgba(79, 70, 229, 0.14);
}

.dialog-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.35;
}

/* ========== Tabs ========== */
.form-tabs {
  padding: 0 4px;
}

.form-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-subtle);
}

.form-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.form-tabs :deep(.el-tabs__item) {
  height: 38px;
  line-height: 38px;
  font-size: 14px;
  font-weight: 500;
  padding: 0 20px;
}

.form-tabs :deep(.el-tabs__content) {
  padding: 12px 20px;
}

.tab-content {
  max-height: calc(100vh - 280px);
  overflow-y: auto;
  min-height: 360px;
}

/* ========== 表单 Section ========== */
.form-section {
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: 10px;
  padding: 10px 14px;
  margin-bottom: 10px;
  transition: border-color 0.2s ease;
}

.form-section:hover {
  border-color: var(--border-default);
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.section-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.section-label-row .section-label {
  margin-bottom: 0;
}

.label-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  flex-shrink: 0;
}

.label-dot.work { background: #6366f1; box-shadow: 0 0 6px rgba(99, 102, 241, 0.5); }
.label-dot.base { background: #3b82f6; box-shadow: 0 0 6px rgba(59, 130, 246, 0.5); }
.label-dot.contact { background: #10b981; box-shadow: 0 0 6px rgba(16, 185, 129, 0.5); }
.label-dot.family { background: #ec4899; box-shadow: 0 0 6px rgba(236, 72, 153, 0.5); }
.label-dot.edu { background: #8b5cf6; box-shadow: 0 0 6px rgba(139, 92, 246, 0.5); }
.label-dot.work-exp { background: #f59e0b; box-shadow: 0 0 6px rgba(245, 158, 11, 0.5); }
.label-dot.cert { background: #14b8a6; box-shadow: 0 0 6px rgba(20, 184, 166, 0.5); }
.label-dot.contract { background: #f97316; box-shadow: 0 0 6px rgba(249, 115, 22, 0.5); }
.label-dot.probation { background: #ef4444; box-shadow: 0 0 6px rgba(239, 68, 68, 0.5); }

/* ========== 表单元素 ========== */
.emp-form :deep(.el-form-item) {
  margin-bottom: 7px;
}

.emp-form :deep(.el-form-item__label) {
  font-size: 13px;
  color: var(--text-secondary);
  white-space: nowrap;
  line-height: 34px;
  padding-right: 9px;
}

.emp-form :deep(.el-input__wrapper) {
  padding: 0 10px;
  height: 34px;
}

.emp-form :deep(.el-input__inner) {
  height: 34px;
  line-height: 34px;
  font-size: 13px;
}

.emp-form :deep(.el-select__wrapper) {
  height: 34px;
}

.emp-form :deep(.el-select__placeholder) {
  font-size: 13px;
}

.emp-form :deep(.el-input-number) {
  height: 34px;
}

.emp-form :deep(.el-input-number .el-input__wrapper) {
  height: 34px;
  padding: 0 8px;
}

.emp-form :deep(.el-date-editor) {
  height: 34px;
  width: 100%;
}

.emp-form :deep(.el-date-editor .el-input__wrapper) {
  height: 34px;
  padding: 0 10px;
}

.emp-form :deep(.el-form-item.is-required .el-form-item__label-wrap .el-form-item__label:not(.is-float)) {
  position: relative;
}

.emp-form :deep(.el-form-item.is-required .el-form-item__label-wrap .el-form-item__label:not(.is-float)::before) {
  content: '*';
  color: #f43f5e;
  font-weight: 500;
  margin-right: 3px;
  font-size: 13px;
}

.info-only :deep(.el-form-item__content) {
  margin-left: 0 !important;
}

.info-only :deep(.el-input) {
  --el-input-bg-color: var(--bg-soft);
}

.full-width {
  width: 100%;
}

.status-radio {
  display: flex;
  gap: 8px;
}

/* ========== 空提示 ========== */
.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: var(--bg-soft);
  border-radius: 8px;
  border: 1px dashed var(--border-default);
  color: var(--text-tertiary);
  font-size: 12.5px;
}

/* ========== 子项卡片 ========== */
.sub-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sub-card {
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  border-radius: 9px;
  padding: 10px 12px;
  transition: all 0.2s ease;
}

.sub-card:hover {
  border-color: var(--border-default);
  box-shadow: 0 2px 8px -2px rgba(0,0,0,0.06);
}

.sub-card.contract {
  border-left: 3px solid #f97316;
}

.sub-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px dashed var(--border-default);
}

.sub-card-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
}

.sub-form :deep(.el-form-item) {
  margin-bottom: 6px;
}

.sub-form :deep(.el-form-item__label) {
  font-size: 12px;
  white-space: nowrap;
}

.sub-form :deep(.el-input__wrapper) {
  padding: 0 10px;
  height: 32px;
}

.sub-form :deep(.el-input__inner) {
  height: 32px;
  line-height: 32px;
  font-size: 13px;
}

.sub-form :deep(.el-select__wrapper) {
  height: 32px;
}

.sub-form :deep(.el-date-editor) {
  height: 32px;
  width: 100%;
}

.sub-form :deep(.el-date-editor .el-input__wrapper) {
  height: 32px;
  padding: 0 10px;
}

/* ========== 家庭成员 ========== */
.family-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.family-card {
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  border-radius: 8px;
  padding: 8px 10px;
}

.family-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
  align-items: flex-start;
}

.family-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.family-form :deep(.el-form-item__label) {
  font-size: 12px;
  white-space: nowrap;
}

.family-form :deep(.el-input__wrapper) {
  padding: 0 10px;
  height: 32px;
}

.family-form :deep(.el-input__inner) {
  height: 32px;
  line-height: 32px;
  font-size: 13px;
}

.family-form :deep(.el-select__wrapper) {
  height: 32px;
}

/* ========== 底部 ========== */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* ========== 响应式 ========== */
@media (max-width: 640px) {
  .dialog-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .quick-tip {
    width: 100%;
  }
}
</style>
