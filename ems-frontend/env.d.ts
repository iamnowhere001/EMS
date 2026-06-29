/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

export {}

declare module 'vue' {
  interface ComponentCustomProperties {
    $hasPermission: (perm: string | string[]) => boolean
    $isAdmin: () => boolean
  }
}
