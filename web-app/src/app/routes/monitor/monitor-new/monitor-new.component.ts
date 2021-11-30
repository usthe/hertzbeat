import { Component, OnInit } from '@angular/core';
import {ParamDefine} from "../../../pojo/ParamDefine";
import {AppDefineService} from "../../../service/app-define.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {switchMap} from "rxjs/operators";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {I18NService} from "@core";
import {Param} from "../../../pojo/Param";
import {Monitor} from "../../../pojo/Monitor";
import {MonitorService} from "../../../service/monitor.service";
import {NzNotificationService} from "ng-zorro-antd/notification";

@Component({
  selector: 'app-monitor-add',
  templateUrl: './monitor-new.component.html',
  styles: [
  ]
})
export class MonitorNewComponent implements OnInit {

  paramDefines!: ParamDefine[];
  params!: Param[];
  monitor!: Monitor;
  profileForm: FormGroup = new FormGroup({});
  detected: boolean = true;
  passwordVisible!: boolean;
  constructor(private appDefineSvc: AppDefineService,
              private monitorSvc: MonitorService,
              private route: ActivatedRoute,
              private router: Router,
              private notifySvc: NzNotificationService,
              private i18n: I18NService,
              private formBuilder: FormBuilder) {
    this.monitor = new Monitor();
  }

  ngOnInit(): void {
    const paramDefine$ = this.route.queryParamMap.pipe(
      switchMap((paramMap: ParamMap) => {
        this.monitor.app = paramMap.get("app") || '';
        return this.appDefineSvc.getAppParamsDefine(this.monitor.app);
      })
    ).subscribe(message => {
      if (message.code === 0) {
        this.paramDefines = message.data;
        this.params = [];
        this.paramDefines.forEach(define => {
          let param = new Param();
          param.field = define.field;
          param.type = define.type === "number" ? 0 : 1;
          this.params.push(param);
        })
      } else {
        console.warn(message.msg);
      }
      paramDefine$.unsubscribe();
    });
  }

  onSubmit() {
    let addMonitor = {
      "detected": this.detected,
      "monitor": this.monitor,
      "params": this.params
    };
    this.monitorSvc.newMonitor(addMonitor)
      .subscribe(message => {
        if (message.code === 0) {
          this.notifySvc.success("新增监控成功", "");
          this.router.navigateByUrl("/monitors")
        } else {
          this.notifySvc.error("新增监控失败", message.msg);
        }
    })
  }

}
