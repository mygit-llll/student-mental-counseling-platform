classDiagram
direction BT
class AbstractService~T~
class Account
class AccountController
class AccountDetailsServiceImpl
class AccountDto
class AccountMapper {
<<Interface>>

}
class AccountRole
class AccountRoleController
class AccountRoleMapper {
<<Interface>>

}
class AccountRoleService {
<<Interface>>

}
class AccountRoleServiceImpl
class AccountService {
<<Interface>>

}
class AccountServiceImpl
class AccountWithRole
class AccountWithRolePermission
class AesUtils
class Application
class AssertUtils
class AuthenticationFilter
class CacheExpire
class ContextUtils
class ControllerLogAspect
class ExceptionResolver
class Handle
class IpUtils
class JasyptConfig
class JsonUtils
class JwtConfigurationProperties
class JwtUtil
class MyAuthenticationEntryPoint
class MyEncryptablePropertyDetector
class MyMapper~T~ {
<<Interface>>

}
class MyRedisCacheManager
class Permission
class PermissionController
class PermissionMapper {
<<Interface>>

}
class PermissionService {
<<Interface>>

}
class PermissionServiceImpl
class ProjectConstant
class RedisCacheConfig
class RedisConfig
class RedisUtils
class RequestWrapper
class Resource
class ResourcesNotFoundException
class Result~T~
class ResultCode {
<<enumeration>>

}
class ResultGenerator
class Role
class RoleController
class RoleMapper {
<<Interface>>

}
class RolePermission
class RolePermissionMapper {
<<Interface>>

}
class RolePermissionService {
<<Interface>>

}
class RolePermissionServiceImpl
class RoleService {
<<Interface>>

}
class RoleServiceImpl
class RoleWithPermission
class RoleWithResource
class RsaConfigurationProperties
class RsaUtils
class Service~T~ {
<<Interface>>

}
class ServiceException
class Swagger2Config
class UrlUtils
class ValidatorConfig
class WebMvcConfig
class WebSecurityConfig

AbstractService~T~  ..>  Service~T~ 
AccountMapper  -->  MyMapper~T~ 
AccountRoleMapper  -->  MyMapper~T~ 
AccountRoleService  -->  Service~T~ 
AccountRoleServiceImpl  -->  AbstractService~T~ 
AccountRoleServiceImpl  ..>  AccountRoleService 
AccountService  -->  Service~T~ 
AccountServiceImpl  -->  AbstractService~T~ 
AccountServiceImpl  ..>  AccountService 
AccountWithRole  -->  Account 
AccountWithRolePermission  -->  AccountWithRole 
PermissionMapper  -->  MyMapper~T~ 
PermissionService  -->  Service~T~ 
PermissionServiceImpl  -->  AbstractService~T~ 
PermissionServiceImpl  ..>  PermissionService 
RoleMapper  -->  MyMapper~T~ 
RolePermissionMapper  -->  MyMapper~T~ 
RolePermissionService  -->  Service~T~ 
RolePermissionServiceImpl  -->  AbstractService~T~ 
RolePermissionServiceImpl  ..>  RolePermissionService 
RoleService  -->  Service~T~ 
RoleServiceImpl  -->  AbstractService~T~ 
RoleServiceImpl  ..>  RoleService 
RoleWithPermission  -->  Role 
RoleWithResource  -->  Role 
